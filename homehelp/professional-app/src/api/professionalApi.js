import apiClient from './axios';
import { STORAGE_KEYS, getStored, setStored } from '../services/mockStorage';
import { WorkerStatus } from '../constants/status';

const USE_FALLBACK = import.meta.env.VITE_ENABLE_MOCK_FALLBACK === 'true';

export const professionalApi = {
  async getProfile() {
    try {
      return await apiClient.get('/professionals/me');
    } catch (err) {
      if (USE_FALLBACK) {
        return { success: true, data: getStored(STORAGE_KEYS.USER, null) };
      }
      throw err;
    }
  },

  async updateProfile(updates) {
    try {
      return await apiClient.patch('/professionals/me', updates);
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, {});
        const updated = { ...user, ...updates };
        setStored(STORAGE_KEYS.USER, updated);
        return { success: true, message: 'Profile updated successfully', data: updated };
      }
      throw err;
    }
  },

  async toggleOnlineStatus(isOnline) {
    try {
      return await apiClient.patch('/professionals/status/online', { isOnline });
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, {});
        // Business Rule: Suspended or blocked professionals cannot go online
        if (user.status === WorkerStatus.SUSPENDED || user.status === WorkerStatus.BLOCKED) {
          throw new Error('Account suspended. You cannot toggle availability.');
        }
        user.isOnline = isOnline;
        setStored(STORAGE_KEYS.USER, user);
        return {
          success: true,
          message: isOnline ? 'You are now Online and eligible for jobs' : 'You are now Offline',
          data: { isOnline }
        };
      }
      throw err;
    }
  },

  async getAvailability() {
    try {
      return await apiClient.get('/professionals/availability');
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, {});
        return { success: true, data: user.availability || {} };
      }
      throw err;
    }
  },

  async updateAvailability(availabilityData) {
    try {
      return await apiClient.put('/professionals/availability', availabilityData);
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, {});
        user.availability = availabilityData;
        setStored(STORAGE_KEYS.USER, user);
        return { success: true, message: 'Availability schedule updated', data: availabilityData };
      }
      throw err;
    }
  },

  async updateServiceAreas(serviceAreas) {
    try {
      return await apiClient.put('/professionals/service-areas', { serviceAreas });
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, {});
        user.serviceAreas = serviceAreas;
        setStored(STORAGE_KEYS.USER, user);
        return { success: true, message: 'Service zones updated', data: serviceAreas };
      }
      throw err;
    }
  }
};
