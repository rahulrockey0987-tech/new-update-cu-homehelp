import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { notificationApi } from '../../api/notificationApi';
import EmptyState from '../../components/common/EmptyState';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { Bell, Briefcase, IndianRupee, ShieldCheck, CheckCheck } from 'lucide-react';

export const NotificationsList = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchNotifs = async () => {
    try {
      setLoading(true);
      const res = await notificationApi.getNotifications();
      if (res.success && res.data) {
        setNotifications(res.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifs();
  }, []);

  const handleMarkAllRead = async () => {
    await notificationApi.markAllAsRead();
    fetchNotifs();
  };

  const handleClickItem = async (notif) => {
    await notificationApi.markAsRead(notif.id);
    if (notif.jobId) {
      navigate(`/jobs/${notif.jobId}`);
    } else if (notif.type === 'payout') {
      navigate('/earnings');
    } else if (notif.type === 'verification') {
      navigate('/kyc');
    } else {
      fetchNotifs();
    }
  };

  const getIcon = (type) => {
    switch (type) {
      case 'job_offer':
        return <Briefcase className="w-4 h-4 text-blue-600" />;
      case 'payout':
        return <IndianRupee className="w-4 h-4 text-emerald-600" />;
      case 'verification':
        return <ShieldCheck className="w-4 h-4 text-purple-600" />;
      default:
        return <Bell className="w-4 h-4 text-slate-600" />;
    }
  };

  if (loading) return <LoadingSpinner message="Fetching alerts & dispatch updates..." />;

  return (
    <div className="p-4 space-y-4 pb-20">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-black text-slate-900 tracking-tight">Notifications</h1>
          <p className="text-xs text-slate-500">Live booking offers, payouts, and system alerts</p>
        </div>

        {notifications.some(n => !n.read) && (
          <button
            onClick={handleMarkAllRead}
            className="flex items-center gap-1 text-xs font-bold text-blue-600 hover:text-blue-800"
          >
            <CheckCheck className="w-4 h-4" />
            <span>Mark all read</span>
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <EmptyState
          icon={Bell}
          title="No notifications"
          description="You are caught up! Job offers and payout transfers will appear here."
        />
      ) : (
        <div className="space-y-2.5">
          {notifications.map(notif => (
            <div
              key={notif.id}
              onClick={() => handleClickItem(notif)}
              className={`p-4 rounded-3xl border transition cursor-pointer flex items-start gap-3.5 ${
                notif.read
                  ? 'bg-white border-slate-200 hover:bg-slate-50'
                  : 'bg-blue-50/60 border-blue-200 hover:bg-blue-50'
              }`}
            >
              <div className="w-9 h-9 rounded-2xl bg-white shadow-sm border border-slate-200 flex items-center justify-center flex-shrink-0 mt-0.5">
                {getIcon(notif.type)}
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between gap-2">
                  <h4 className={`text-xs font-bold ${notif.read ? 'text-slate-800' : 'text-blue-950'}`}>
                    {notif.title}
                  </h4>
                  {!notif.read && (
                    <span className="w-2 h-2 rounded-full bg-blue-600 flex-shrink-0" />
                  )}
                </div>
                <p className="text-xs text-slate-600 mt-0.5 leading-relaxed">{notif.message}</p>
                <span className="text-[10px] text-slate-400 mt-1.5 block">
                  {new Date(notif.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
export default NotificationsList;
