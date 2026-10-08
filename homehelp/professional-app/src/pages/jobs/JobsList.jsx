import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { jobApi } from '../../api/jobApi';
import StatusBadge from '../../components/common/StatusBadge';
import EmptyState from '../../components/common/EmptyState';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { JobStatus } from '../../constants/status';
import {
  Briefcase, Calendar, Clock, MapPin, IndianRupee,
  Navigation, ChevronRight, CheckCircle2
} from 'lucide-react';

export const JobsList = () => {
  const [activeTab, setActiveTab] = useState('all'); // all, today, upcoming, completed
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchJobs = async () => {
      try {
        setLoading(true);
        const res = await jobApi.getJobs();
        if (res.success && res.data) {
          setJobs(res.data);
        }
      } catch (err) {
        console.error('Fetch jobs error', err);
      } finally {
        setLoading(false);
      }
    };
    fetchJobs();
  }, []);

  const todayStr = new Date().toISOString().split('T')[0];

  const filteredJobs = jobs.filter(job => {
    if (activeTab === 'today') {
      return job.scheduledDate === todayStr && job.status !== JobStatus.COMPLETED;
    }
    if (activeTab === 'upcoming') {
      return job.scheduledDate > todayStr || ([JobStatus.ASSIGNED, JobStatus.ACCEPTED].includes(job.status) && job.scheduledDate >= todayStr);
    }
    if (activeTab === 'completed') {
      return job.status === JobStatus.COMPLETED;
    }
    return true; // all
  });

  return (
    <div className="p-4 space-y-4">
      {/* Header */}
      <div>
        <h1 className="text-xl font-black text-slate-900 tracking-tight">Assigned Jobs</h1>
        <p className="text-xs text-slate-500">Your scheduled customer appointments and bookings</p>
      </div>

      {/* Tabs */}
      <div className="flex bg-slate-200/80 p-1 rounded-2xl text-xs font-bold text-slate-600">
        {[
          { id: 'all', label: 'All Jobs' },
          { id: 'today', label: "Today" },
          { id: 'upcoming', label: 'Upcoming' },
          { id: 'completed', label: 'Completed' }
        ].map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex-1 py-2 rounded-xl transition ${
              activeTab === tab.id ? 'bg-white text-blue-600 shadow-sm' : 'hover:text-slate-900'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* List */}
      {loading ? (
        <LoadingSpinner message="Fetching job bookings..." />
      ) : filteredJobs.length === 0 ? (
        <EmptyState
          icon={Briefcase}
          title="No jobs scheduled"
          description={`No jobs found for the selected "${activeTab}" filter.`}
          actionLabel="View All Jobs"
          onAction={() => setActiveTab('all')}
        />
      ) : (
        <div className="space-y-3">
          {filteredJobs.map(job => (
            <div
              key={job.id}
              onClick={() => navigate(`/jobs/${job.id}`)}
              className="bg-white rounded-3xl p-4 border border-slate-200 shadow-sm hover:border-blue-400 cursor-pointer transition active:scale-[0.99]"
            >
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-mono font-bold text-slate-400">{job.id}</span>
                <StatusBadge status={job.status} type="job" />
              </div>

              <h3 className="text-sm font-bold text-slate-900 mb-1 leading-snug">
                {job.serviceTitle}
              </h3>

              <div className="flex items-center gap-1.5 text-xs text-slate-500 mb-3">
                <MapPin className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                <span className="truncate">{job.locality}</span>
                {job.distanceKm && (
                  <span className="text-slate-400">({job.distanceKm} km away)</span>
                )}
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-slate-100 text-xs">
                <div className="flex items-center gap-3 text-slate-600 font-medium">
                  <span className="flex items-center gap-1">
                    <Calendar className="w-3.5 h-3.5 text-slate-400" />
                    <span>{job.scheduledDate === todayStr ? 'Today' : job.scheduledDate}</span>
                  </span>
                  <span className="flex items-center gap-1">
                    <Clock className="w-3.5 h-3.5 text-slate-400" />
                    <span>{job.scheduledTime}</span>
                  </span>
                </div>

                <div className="flex items-center gap-1 font-bold text-emerald-600 text-sm">
                  <span>₹{job.earnings}</span>
                  <ChevronRight className="w-4 h-4 text-slate-300" />
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
export default JobsList;
