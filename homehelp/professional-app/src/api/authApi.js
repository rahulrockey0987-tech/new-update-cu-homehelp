import apiClient from './axios';
import { STORAGE_KEYS, getStored, setStored } from '../services/mockStorage';
import { WorkerStatus, KycStatus } from '../constants/status';

const USE_FALLBACK = import.meta.env.VITE_ENABLE_MOCK_FALLBACK === 'true';

export const authApi = {
  async login(credentials) {
    try {
      return await apiClient.post('/professionals/login', credentials);
    } catch (err) {
      if (USE_FALLBACK) {
        // Mock fallback simulation
        const user = getStored(STORAGE_KEYS.USER, null);
        if (user && (credentials.identifier === user.phone || credentials.identifier === user.email || credentials.email === user.email)) {
          const token = 'jwt_token_sample_worker_verified_prod';
          setStored(STORAGE_KEYS.TOKEN, token);
          return {
            success: true,
            message: 'Login successful',
            data: { user, token }
          };
        }
        // Fallback default worker login
        const defaultUser = {
          id: 'pro_' + Math.floor(100000 + Math.random() * 900000),
          fullName: 'Professional Worker',
          email: credentials.email || credentials.identifier || 'worker@homehelp.pro',
          phone: credentials.phone || credentials.identifier || '+91 98765 00000',
          city: 'Hyderabad',
          serviceCategory: 'ac-repair',
          skills: ['AC Repair', 'Electrical'],
          experienceYears: 4,
          languages: ['Telugu', 'Hindi'],
          status: WorkerStatus.ACTIVE,
          kycStatus: KycStatus.VERIFIED,
          isOnline: true,
          rating: 4.85,
          totalJobsCompleted: 88,
          serviceAreas: ['hyd-madhapur', 'hyd-gachibowli']
        };
        setStored(STORAGE_KEYS.USER, defaultUser);
        setStored(STORAGE_KEYS.TOKEN, 'jwt_token_sample_worker_verified_prod');
        return {
          success: true,
          message: 'Login successful (simulated)',
          data: { user: defaultUser, token: 'jwt_token_sample_worker_verified_prod' }
        };
      }
      throw err;
    }
  },

  async register(registrationData) {
    try {
      return await apiClient.post('/professionals/register', registrationData);
    } catch (err) {
      if (USE_FALLBACK) {
        // Enforce business rule: A worker is NOT automatically verified!
        const newUser = {
          id: 'pro_' + Math.floor(100000 + Math.random() * 900000),
          fullName: registrationData.fullName,
          phone: registrationData.phone,
          email: registrationData.email,
          city: registrationData.city || 'Hyderabad',
          serviceCategory: registrationData.serviceCategory,
          skills: registrationData.skills || [],
          experienceYears: Number(registrationData.experienceYears || 1),
          status: WorkerStatus.REGISTERED, // Explicit business rule
          kycStatus: KycStatus.NOT_STARTED,
          isOnline: false,
          rating: 5.0,
          totalJobsCompleted: 0,
          acceptanceRate: 100,
          serviceAreas: registrationData.serviceAreas || [],
          createdAt: new Date().toISOString()
        };
        setStored(STORAGE_KEYS.USER, newUser);
        setStored(STORAGE_KEYS.TOKEN, 'jwt_token_new_user');
        return {
          success: true,
          message: 'Registration successful. Please complete your profile and KYC.',
          data: { user: newUser, token: 'jwt_token_new_user' }
        };
      }
      throw err;
    }
  },

  async getMe() {
    try {
      return await apiClient.get('/professionals/me');
    } catch (err) {
      if (USE_FALLBACK) {
        const user = getStored(STORAGE_KEYS.USER, null);
        return {
          success: true,
          message: 'Profile retrieved',
          data: user
        };
      }
      throw err;
    }
  },

  async logout() {
    try {
      await apiClient.post('/professionals/logout');
    } catch (e) {
      // Ignore network errors on logout
    } finally {
      localStorage.removeItem(STORAGE_KEYS.TOKEN);
    }
    return { success: true, message: 'Logged out successfully' };
  }
};
