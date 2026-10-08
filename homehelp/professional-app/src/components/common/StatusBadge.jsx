import React from 'react';
import { WorkerStatus, KycStatus, JobStatus } from '../../constants/status';

export const StatusBadge = ({ status, type = 'worker', size = 'sm' }) => {
  const getDetails = () => {
    switch (status) {
      // Worker Statuses
      case WorkerStatus.ACTIVE:
        return { label: 'Active & Verified', bg: 'bg-emerald-50 text-emerald-700 border-emerald-200', dot: 'bg-emerald-500' };
      case WorkerStatus.VERIFIED:
        return { label: 'Verified', bg: 'bg-blue-50 text-blue-700 border-blue-200', dot: 'bg-blue-500' };
      case WorkerStatus.UNDER_REVIEW:
        return { label: 'Under Review', bg: 'bg-amber-50 text-amber-700 border-amber-200', dot: 'bg-amber-500' };
      case WorkerStatus.REGISTERED:
      case WorkerStatus.PROFILE_COMPLETED:
        return { label: 'Setup Incomplete', bg: 'bg-slate-100 text-slate-700 border-slate-300', dot: 'bg-slate-400' };
      case WorkerStatus.SUSPENDED:
        return { label: 'Suspended', bg: 'bg-rose-50 text-rose-700 border-rose-200', dot: 'bg-rose-500' };

      // KYC Statuses
      case KycStatus.VERIFIED:
        return { label: 'KYC Approved', bg: 'bg-emerald-50 text-emerald-700 border-emerald-200', dot: 'bg-emerald-500' };
      case KycStatus.UNDER_REVIEW:
      case KycStatus.SUBMITTED:
        return { label: 'KYC Pending Review', bg: 'bg-amber-50 text-amber-700 border-amber-200', dot: 'bg-amber-500' };
      case KycStatus.REJECTED:
        return { label: 'KYC Rejected', bg: 'bg-rose-50 text-rose-700 border-rose-200', dot: 'bg-rose-500' };
      case KycStatus.RESUBMISSION_REQUIRED:
        return { label: 'Action Required', bg: 'bg-orange-50 text-orange-700 border-orange-200', dot: 'bg-orange-500' };
      case KycStatus.NOT_STARTED:
      case KycStatus.DRAFT:
        return { label: 'KYC Not Started', bg: 'bg-slate-100 text-slate-600 border-slate-300', dot: 'bg-slate-400' };

      // Job Statuses
      case JobStatus.OFFERED:
      case JobStatus.ASSIGNED:
        return { label: 'New Job Offer', bg: 'bg-blue-50 text-blue-700 border-blue-200', dot: 'bg-blue-500' };
      case JobStatus.ACCEPTED:
        return { label: 'Accepted', bg: 'bg-indigo-50 text-indigo-700 border-indigo-200', dot: 'bg-indigo-500' };
      case JobStatus.EN_ROUTE:
        return { label: 'En Route', bg: 'bg-purple-50 text-purple-700 border-purple-200', dot: 'bg-purple-500' };
      case JobStatus.ARRIVED:
        return { label: 'Arrived (OTP Pending)', bg: 'bg-amber-50 text-amber-700 border-amber-200', dot: 'bg-amber-500' };
      case JobStatus.OTP_VERIFIED:
        return { label: 'OTP Verified', bg: 'bg-teal-50 text-teal-700 border-teal-200', dot: 'bg-teal-500' };
      case JobStatus.SERVICE_STARTED:
        return { label: 'In Progress', bg: 'bg-emerald-50 text-emerald-700 border-emerald-300', dot: 'bg-emerald-500' };
      case JobStatus.COMPLETED:
        return { label: 'Completed', bg: 'bg-slate-100 text-slate-700 border-slate-200', dot: 'bg-slate-400' };
      case JobStatus.CANCELLED:
      case JobStatus.REJECTED:
        return { label: 'Cancelled', bg: 'bg-rose-50 text-rose-700 border-rose-200', dot: 'bg-rose-400' };

      default:
        return { label: status || 'Unknown', bg: 'bg-slate-100 text-slate-700 border-slate-200', dot: 'bg-slate-400' };
    }
  };

  const { label, bg, dot } = getDetails();
  const sizeClasses = size === 'sm' ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-sm font-medium';

  return (
    <span className={`inline-flex items-center gap-1.5 rounded-full border font-medium ${bg} ${sizeClasses}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${dot}`} />
      <span>{label}</span>
    </span>
  );
};
export default StatusBadge;
