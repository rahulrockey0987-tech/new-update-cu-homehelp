import apiClient from './axios';
import { STORAGE_KEYS, getStored, setStored } from '../services/mockStorage';
import { PayoutStatus } from '../constants/status';

const USE_FALLBACK = import.meta.env.VITE_ENABLE_MOCK_FALLBACK === 'true';

export const payoutApi = {
  async getPayouts() {
    try {
      return await apiClient.get('/professional/payouts');
    } catch (err) {
      if (USE_FALLBACK) {
        const payouts = getStored(STORAGE_KEYS.PAYOUTS, [
          {
            id: 'PAY-9012',
            amount: 4820,
            status: PayoutStatus.PAID,
            date: '2026-09-28',
            referenceId: 'UPI-NEFT-88912903',
            bankMasked: 'HDFC (ending in 9284)'
          },
          {
            id: 'PAY-8910',
            amount: 6350,
            status: PayoutStatus.PAID,
            date: '2026-09-21',
            referenceId: 'UPI-NEFT-77182931',
            bankMasked: 'HDFC (ending in 9284)'
          },
          {
            id: 'PAY-9104',
            amount: 3700,
            status: PayoutStatus.PROCESSING,
            date: '2026-10-01',
            referenceId: 'BATCH-PROC-091',
            bankMasked: 'HDFC (ending in 9284)'
          }
        ]);
        return { success: true, data: payouts };
      }
      throw err;
    }
  },

  async getBankDetails() {
    try {
      return await apiClient.get('/professionals/bank');
    } catch (err) {
      if (USE_FALLBACK) {
        const bank = getStored(STORAGE_KEYS.BANK, {});
        return { success: true, data: bank };
      }
      throw err;
    }
  },

  async updateBankDetails(bankDetails) {
    try {
      return await apiClient.put('/professionals/bank', bankDetails);
    } catch (err) {
      if (USE_FALLBACK) {
        const masked = 'XXXXXXXX' + (bankDetails.accountNumber ? bankDetails.accountNumber.slice(-4) : '0000');
        const updated = {
          accountHolder: bankDetails.accountHolder,
          bankName: bankDetails.bankName || 'Verified Bank',
          accountNumber: bankDetails.accountNumber,
          maskedAccount: masked,
          ifsc: bankDetails.ifsc.toUpperCase(),
          isVerified: false // Requires admin verification
        };
        setStored(STORAGE_KEYS.BANK, updated);
        return {
          success: true,
          message: 'Bank details submitted. Penny-drop / admin verification in progress.',
          data: updated
        };
      }
      throw err;
    }
  }
};
