import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authApi } from '../api/authApi';
import { professionalApi } from '../api/professionalApi';
import { initStorage, getStored, STORAGE_KEYS } from '../services/mockStorage';
import { WorkerStatus, KycStatus } from '../constants/status';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [authError, setAuthError] = useState(null);

  const loadCurrentSession = useCallback(async () => {
    try {
      setLoading(true);
      initStorage();
      const token = getStored(STORAGE_KEYS.TOKEN, null);
      if (token) {
        const res = await authApi.getMe();
        if (res.data) {
          setUser(res.data);
        }
      } else {
        // Fallback for preview/development initial load
        const storedUser = getStored(STORAGE_KEYS.USER, null);
        if (storedUser) {
          setUser(storedUser);
        }
      }
    } catch (err) {
      console.warn('Session load warning:', err.message);
      const fallbackUser = getStored(STORAGE_KEYS.USER, null);
      if (fallbackUser) setUser(fallbackUser);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadCurrentSession();
  }, [loadCurrentSession]);

  const login = async (credentials) => {
    setLoading(true);
    setAuthError(null);
    try {
      const res = await authApi.login(credentials);
      if (res.success && res.data?.user) {
        setUser(res.data.user);
        return { success: true, user: res.data.user };
      }
      throw new Error(res.message || 'Login failed');
    } catch (err) {
      setAuthError(err.message || 'Failed to authenticate');
      return { success: false, error: err.message };
    } finally {
      setLoading(false);
    }
  };

  const register = async (regData) => {
    setLoading(true);
    setAuthError(null);
    try {
      const res = await authApi.register(regData);
      if (res.success && res.data?.user) {
        setUser(res.data.user);
        return { success: true, user: res.data.user };
      }
      throw new Error(res.message || 'Registration failed');
    } catch (err) {
      setAuthError(err.message || 'Failed to register');
      return { success: false, error: err.message };
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      setUser(null);
    }
  };

  const toggleOnline = async () => {
    if (!user) return;
    const targetStatus = !user.isOnline;
    try {
      const res = await professionalApi.toggleOnlineStatus(targetStatus);
      if (res.success) {
        setUser(prev => ({ ...prev, isOnline: targetStatus }));
        return { success: true, isOnline: targetStatus };
      }
    } catch (err) {
      return { success: false, error: err.message };
    }
  };

  const refreshProfile = async () => {
    try {
      const res = await professionalApi.getProfile();
      if (res.success && res.data) {
        setUser(res.data);
      }
    } catch (err) {
      console.warn('Failed to refresh profile:', err.message);
    }
  };

  const isVerifiedWorker = user && (user.status === WorkerStatus.ACTIVE || user.status === WorkerStatus.VERIFIED);
  const isKycVerified = user && user.kycStatus === KycStatus.VERIFIED;

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        authError,
        isVerifiedWorker,
        isKycVerified,
        login,
        register,
        logout,
        toggleOnline,
        refreshProfile
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
