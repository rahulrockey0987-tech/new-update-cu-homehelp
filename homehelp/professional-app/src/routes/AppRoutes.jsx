import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../layouts/AppLayout';

// Pages
import Login from '../pages/auth/Login';
import Register from '../pages/auth/Register';
import ForgotPassword from '../pages/auth/ForgotPassword';
import OnboardingFlow from '../pages/onboarding/OnboardingFlow';
import Dashboard from '../pages/dashboard/Dashboard';
import JobsList from '../pages/jobs/JobsList';
import JobDetail from '../pages/jobs/JobDetail';
import EarningsDashboard from '../pages/earnings/EarningsDashboard';
import Profile from '../pages/profile/Profile';
import KycStatusPage from '../pages/profile/KycStatusPage';
import AvailabilityPage from '../pages/profile/AvailabilityPage';
import ServiceAreasPage from '../pages/profile/ServiceAreasPage';
import BankDetailsPage from '../pages/profile/BankDetailsPage';
import NotificationsList from '../pages/notifications/NotificationsList';
import SupportTickets from '../pages/support/SupportTickets';
import LoadingSpinner from '../components/common/LoadingSpinner';

const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner message="Checking partner credentials..." fullScreen />;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Auth Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route
        path="/onboarding"
        element={
          <ProtectedRoute>
            <OnboardingFlow />
          </ProtectedRoute>
        }
      />

      {/* Main App Layout */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="jobs" element={<JobsList />} />
        <Route path="jobs/:jobId" element={<JobDetail />} />
        <Route path="earnings" element={<EarningsDashboard />} />
        <Route path="notifications" element={<NotificationsList />} />
        <Route path="profile" element={<Profile />} />
        <Route path="kyc" element={<KycStatusPage />} />
        <Route path="profile/availability" element={<AvailabilityPage />} />
        <Route path="profile/service-areas" element={<ServiceAreasPage />} />
        <Route path="profile/bank" element={<BankDetailsPage />} />
        <Route path="support" element={<SupportTickets />} />
      </Route>

      {/* Catch-all */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};
export default AppRoutes;
