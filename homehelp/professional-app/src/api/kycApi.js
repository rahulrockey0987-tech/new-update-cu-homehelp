import apiClient from './axios';
import { STORAGE_KEYS, getStored, setStored } from '../services/mockStorage';
import { WorkerStatus, KycStatus } from '../constants/status';

const USE_FALLBACK = import.meta.env.VITE_ENABLE_MOCK_FALLBACK === 'true';

export const kycApi = {
  async getKycStatus() {
    try {
      return await apiClient.get('/professionals/kyc');
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, {});
        const kycData = getStored(STORAGE_KEYS.KYC, {
          status: user.kycStatus || KycStatus.NOT_STARTED,
          submittedAt: user.kycStatus === KycStatus.VERIFIED ? '2026-08-15T10:00:00Z' : null,
          documents: [
            { type: 'aadhaar', number: 'XXXX-XXXX-8921', status: 'VERIFIED', verifiedAt: '2026-08-15' },
            { type: 'pan', number: 'ABCDE1234F', status: 'VERIFIED', verifiedAt: '2026-08-15' },
            { type: 'bank_proof', status: 'VERIFIED', verifiedAt: '2026-08-15' }
          ],
          reviewNotes: null
        });
        return { success: true, data: kycData };
      }
      throw err;
    }
  },

  async submitKyc(payload) {
    try {
      return await apiClient.post('/professionals/kyc', payload);
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, {});
        // Business Rule: Worker cannot approve their own KYC. It transitions to UNDER_REVIEW!
        user.kycStatus = KycStatus.UNDER_REVIEW;
        user.status = WorkerStatus.UNDER_REVIEW;
        setStored(STORAGE_KEYS.USER, user);

        const newKyc = {
          status: KycStatus.UNDER_REVIEW,
          submittedAt: new Date().toISOString(),
          documents: payload.documents || [],
          reviewNotes: 'Documents submitted. Verification team is reviewing within 24-48 business hours.'
        };
        setStored(STORAGE_KEYS.KYC, newKyc);

        return {
          success: true,
          message: 'KYC documents submitted for review. An administrator will verify them.',
          data: newKyc
        };
      }
      throw err;
    }
  }
};
