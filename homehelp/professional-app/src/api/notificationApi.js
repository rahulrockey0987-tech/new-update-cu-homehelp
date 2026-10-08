import apiClient from './axios';
import { STORAGE_KEYS, getStored, setStored } from '../services/mockStorage';

const USE_FALLBACK = import.meta.env.VITE_ENABLE_MOCK_FALLBACK === 'true';

export const notificationApi = {
  async getNotifications() {
    try {
      return await apiClient.get('/professional/notifications');
    } catch (err) {
      if (USE_FALLBACK) {
        return { success: true, data: getStored(STORAGE_KEYS.NOTIFICATIONS, []) };
      }
      throw err;
    }
  },

  async markAsRead(notificationId) {
    try {
      return await apiClient.patch(`/professional/notifications/${notificationId}/read`);
    } catch (err) {
      if (USE_FALLBACK) {
        const notifs = getStored(STORAGE_KEYS.NOTIFICATIONS, []);
        const updated = notifs.map(n => n.id === notificationId ? { ...n, read: true } : n);
        setStored(STORAGE_KEYS.NOTIFICATIONS, updated);
        return { success: true, message: 'Notification marked read' };
      }
      throw err;
    }
  },

  async markAllAsRead() {
    try {
      return await apiClient.patch('/professional/notifications/read-all');
    } catch (err) {
      if (USE_FALLBACK) {
        const notifs = getStored(STORAGE_KEYS.NOTIFICATIONS, []);
        const updated = notifs.map(n => ({ ...n, read: true }));
        setStored(STORAGE_KEYS.NOTIFICATIONS, updated);
        return { success: true, message: 'All notifications marked read' };
      }
      throw err;
    }
  }
};
