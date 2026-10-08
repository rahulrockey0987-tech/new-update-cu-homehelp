import apiClient from './axios';
import { STORAGE_KEYS, getStored } from '../services/mockStorage';
import { JobStatus } from '../constants/status';

const USE_FALLBACK = import.meta.env.VITE_ENABLE_MOCK_FALLBACK === 'true';

export const earningsApi = {
  async getEarningsSummary() {
    try {
      return await apiClient.get('/professional/earnings');
    } catch (err) {
      if (USE_FALLBACK) {
        const jobs = getStored(STORAGE_KEYS.JOBS, []);
        const completedJobs = jobs.filter(j => j.status === JobStatus.COMPLETED);
        
        const totalEarnings = completedJobs.reduce((acc, curr) => acc + (curr.earnings || 0), 18450);
        const todayEarnings = 1850;
        const weeklyEarnings = 9200;
        const monthlyEarnings = 36800;
        const pendingPayout = 3700;
        const totalPaid = totalEarnings - pendingPayout;

        return {
          success: true,
          data: {
            todayEarnings,
            weeklyEarnings,
            monthlyEarnings,
            lifetimeGross: Math.round(totalEarnings * 1.25),
            lifetimePlatformFee: Math.round(totalEarnings * 0.25),
            lifetimeNet: totalEarnings,
            pendingPayout,
            totalPaid,
            recentJobEarnings: completedJobs.map(j => ({
              jobId: j.id,
              serviceTitle: j.serviceTitle,
              date: j.scheduledDate,
              earnings: j.earnings,
              grossPrice: j.grossPrice,
              platformFee: (j.grossPrice || 0) - (j.earnings || 0)
            }))
          }
        };
      }
      throw err;
    }
  }
};
