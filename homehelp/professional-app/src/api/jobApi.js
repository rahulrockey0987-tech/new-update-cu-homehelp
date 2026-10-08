import apiClient from './axios';
import { STORAGE_KEYS, getStored, setStored } from '../services/mockStorage';
import { JobStatus, WorkerStatus } from '../constants/status';

const USE_FALLBACK = import.meta.env.VITE_ENABLE_MOCK_FALLBACK === 'true';

export const jobApi = {
  async getJobs(params = {}) {
    try {
      return await apiClient.get('/professional/jobs', { params });
    } catch (err) {
      if (USE_FALLBACK) {
        let jobs = getStored(STORAGE_KEYS.JOBS, []);
        if (params.status) {
          jobs = jobs.filter(j => j.status === params.status);
        }
        return { success: true, data: jobs };
      }
      throw err;
    }
  },

  async getJobById(id) {
    try {
      return await apiClient.get(`/professional/jobs/${id}`);
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const job = jobs.find(j => j.id === id);
        if (!job) {
          throw { status: 404, message: 'Job not found' };
        }
        return { success: true, data: job };
      }
      throw err;
    }
  },

  async acceptJob(jobId) {
    try {
      return await apiClient.post(`/professional/jobs/${jobId}/accept`);
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, {});
        // Server validation rule: Only verified and active professionals can accept
        if (user.status !== WorkerStatus.ACTIVE && user.status !== WorkerStatus.VERIFIED) {
          throw { message: 'You must be a verified and active professional to accept jobs.' };
        }

        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const index = jobs.findIndex(j => j.id === jobId);
        if (index === -1) {
          throw { code: 'JOB_NOT_FOUND', message: 'Job does not exist.' };
        }

        const targetJob = jobs[index];
        if (targetJob.status !== JobStatus.OFFERED && targetJob.status !== JobStatus.ASSIGNED) {
          throw { code: 'JOB_NO_LONGER_AVAILABLE', message: 'This job was accepted by another professional or expired.' };
        }

        targetJob.status = JobStatus.ACCEPTED;
        targetJob.acceptedAt = new Date().toISOString();
        jobs[index] = targetJob;
        setStored(STORAGE_KEYS.JOBS, jobs);

        return {
          success: true,
          message: 'Job accepted successfully. Proceed to customer location when scheduled.',
          data: targetJob
        };
      }
      throw err;
    }
  },

  async rejectJob(jobId, reason) {
    try {
      return await apiClient.post(`/professional/jobs/${jobId}/reject`, { reason });
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const updated = jobs.map(j => {
          if (j.id === jobId) {
            return { ...j, status: JobStatus.REJECTED, rejectionReason: reason };
          }
          return j;
        });
        setStored(STORAGE_KEYS.JOBS, updated);
        return { success: true, message: 'Job declined.' };
      }
      throw err;
    }
  },

  async startEnRoute(jobId) {
    try {
      return await apiClient.post(`/professional/jobs/${jobId}/en-route`);
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const job = jobs.find(j => j.id === jobId);
        if (!job) throw { message: 'Job not found' };
        job.status = JobStatus.EN_ROUTE;
        job.enRouteAt = new Date().toISOString();
        setStored(STORAGE_KEYS.JOBS, jobs);
        return { success: true, message: 'Status updated to En Route', data: job };
      }
      throw err;
    }
  },

  async markArrived(jobId) {
    try {
      return await apiClient.post(`/professional/jobs/${jobId}/arrive`);
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const job = jobs.find(j => j.id === jobId);
        if (!job) throw { message: 'Job not found' };
        job.status = JobStatus.ARRIVED;
        job.arrivedAt = new Date().toISOString();
        setStored(STORAGE_KEYS.JOBS, jobs);
        return {
          success: true,
          message: 'Arrival confirmed. Request customer OTP to begin service.',
          data: job
        };
      }
      throw err;
    }
  },

  async verifyOtp(jobId, otpInput) {
    try {
      return await apiClient.post(`/professional/jobs/${jobId}/verify-otp`, { otp: otpInput });
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const job = jobs.find(j => j.id === jobId);
        if (!job) throw { message: 'Job not found' };
        
        // Validate OTP against server/booking record
        if (job.otp !== otpInput.trim()) {
          throw { code: 'INVALID_OTP', message: 'Incorrect OTP. Please ask customer for the 4-digit code.' };
        }

        job.status = JobStatus.OTP_VERIFIED;
        job.otpVerifiedAt = new Date().toISOString();
        setStored(STORAGE_KEYS.JOBS, jobs);
        return {
          success: true,
          message: 'OTP verified successfully. You may now start the service.',
          data: job
        };
      }
      throw err;
    }
  },

  async startService(jobId) {
    try {
      return await apiClient.post(`/professional/jobs/${jobId}/start`);
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const job = jobs.find(j => j.id === jobId);
        if (!job) throw { message: 'Job not found' };

        if (job.status !== JobStatus.OTP_VERIFIED) {
          throw { message: 'Cannot start service without verified customer OTP.' };
        }

        job.status = JobStatus.SERVICE_STARTED;
        job.serviceStartedAt = new Date().toISOString();
        setStored(STORAGE_KEYS.JOBS, jobs);
        return { success: true, message: 'Service in progress.', data: job };
      }
      throw err;
    }
  },

  async completeService(jobId, completionData = {}) {
    try {
      return await apiClient.post(`/professional/jobs/${jobId}/complete`, completionData);
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const job = jobs.find(j => j.id === jobId);
        if (!job) throw { message: 'Job not found' };

        job.status = JobStatus.COMPLETED;
        job.completedAt = new Date().toISOString();
        job.proofPhotos = completionData.proofPhotos || job.proofPhotos || [];
        job.notes = completionData.notes || '';

        // Update professional stats
        const user = getStored(STORAGE_KEYS.USER, {});
        user.totalJobsCompleted = (user.totalJobsCompleted || 0) + 1;
        setStored(STORAGE_KEYS.USER, user);
        setStored(STORAGE_KEYS.JOBS, jobs);

        return {
          success: true,
          message: 'Service marked as completed! Job finalized.',
          data: job
        };
      }
      throw err;
    }
  },

  async requestAdditionalCharge(jobId, chargePayload) {
    try {
      return await apiClient.post(`/professional/jobs/${jobId}/additional-charge`, chargePayload);
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const job = jobs.find(j => j.id === jobId);
        if (!job) throw { message: 'Job not found' };

        const newCharge = {
          id: 'CHG-' + Math.floor(1000 + Math.random() * 9000),
          title: chargePayload.title,
          amount: Number(chargePayload.amount),
          reason: chargePayload.reason,
          status: 'PENDING_APPROVAL',
          requestedAt: new Date().toISOString()
        };

        job.additionalCharges = job.additionalCharges || [];
        job.additionalCharges.push(newCharge);
        setStored(STORAGE_KEYS.JOBS, jobs);

        return {
          success: true,
          message: 'Additional charge request sent to customer for in-app approval.',
          data: newCharge
        };
      }
      throw err;
    }
  }
};
