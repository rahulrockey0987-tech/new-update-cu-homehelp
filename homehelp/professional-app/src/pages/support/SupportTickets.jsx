import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { supportApi } from '../../api/supportApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import SosModal from '../../components/common/SosModal';
import {
  ArrowLeft, Headphones, Plus, ShieldAlert, CheckCircle2,
  Clock, MessageSquare, PhoneCall, AlertTriangle
} from 'lucide-react';

export const SupportTickets = () => {
  const navigate = useNavigate();
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [sosOpen, setSosOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [newTicket, setNewTicket] = useState({
    category: 'Job & Location Dispute',
    subject: '',
    bookingReference: '',
    description: ''
  });

  const categories = [
    'Customer Unavailable / Gate Locked',
    'Incorrect Address / Unreachable Location',
    'Safety Concern / Harassment',
    'Payment / Payout Query',
    'Booking Cancellation by Customer',
    'Spare Parts / Additional Charge Issue',
    'KYC / Document Verification Problem'
  ];

  const fetchTickets = async () => {
    try {
      setLoading(true);
      const res = await supportApi.getTickets();
      if (res.success && res.data) {
        setTickets(res.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTickets();
  }, []);

  const handleCreateTicket = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const res = await supportApi.createTicket(newTicket);
      if (res.success) {
        setShowModal(false);
        setNewTicket({ category: categories[0], subject: '', bookingReference: '', description: '' });
        fetchTickets();
      }
    } catch (e) {
      console.error(e);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner message="Connecting to partner help desk..." />;

  return (
    <div className="p-4 space-y-4 pb-20">
      <div className="flex items-center justify-between">
        <button
          onClick={() => navigate('/profile')}
          className="flex items-center gap-1 text-xs font-bold text-slate-600 hover:text-slate-900"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Profile</span>
        </button>
        <button
          onClick={() => setSosOpen(true)}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-red-50 text-red-600 border border-red-200 text-xs font-bold active:scale-95"
        >
          <ShieldAlert className="w-4 h-4" />
          <span>Safety SOS</span>
        </button>
      </div>

      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-black text-slate-900 tracking-tight">Partner Support</h1>
          <p className="text-xs text-slate-500">Dedicated operational and dispute assistance</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="px-3.5 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold flex items-center gap-1 shadow-sm transition active:scale-95"
        >
          <Plus className="w-4 h-4" />
          <span>New Ticket</span>
        </button>
      </div>

      {/* Immediate Helpline banner */}
      <div className="bg-slate-900 text-white p-4 rounded-3xl flex items-center justify-between shadow-sm">
        <div>
          <span className="text-[10px] uppercase font-bold text-blue-300">Partner Helpline</span>
          <h4 className="text-sm font-bold mt-0.5">Hyderabad Partner Care</h4>
          <span className="text-xs text-slate-400">Available 07:00 AM – 10:00 PM</span>
        </div>
        <a
          href="tel:+9118002004499"
          className="px-3.5 py-2 bg-blue-600 hover:bg-blue-500 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 shadow-md shadow-blue-500/30"
        >
          <PhoneCall className="w-4 h-4" />
          <span>Call Desk</span>
        </a>
      </div>

      {/* Tickets List */}
      <div className="space-y-3">
        <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500">
          Support History & Open Inquiries
        </h3>

        {tickets.length === 0 ? (
          <EmptyState
            icon={Headphones}
            title="No support tickets"
            description="You don't have any active disputes or inquiries."
            actionLabel="Create Support Ticket"
            onAction={() => setShowModal(true)}
          />
        ) : (
          tickets.map(t => (
            <div key={t.id} className="bg-white rounded-3xl p-4 border border-slate-200 shadow-sm space-y-2">
              <div className="flex items-center justify-between text-xs">
                <span className="font-mono font-bold text-slate-400">{t.id}</span>
                <span className={`px-2 py-0.5 rounded-full font-bold text-[10px] ${
                  t.status === 'RESOLVED' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-blue-50 text-blue-700 border border-blue-200'
                }`}>
                  {t.status}
                </span>
              </div>

              <div>
                <h4 className="text-xs font-bold text-slate-900">{t.subject || t.category}</h4>
                <p className="text-[11px] text-slate-500">{t.category}</p>
              </div>

              {t.messages && t.messages.length > 0 && (
                <div className="bg-slate-50 p-2.5 rounded-xl border border-slate-100 text-xs text-slate-700 mt-2">
                  <span className="font-bold text-[11px] text-slate-500 block mb-0.5">Latest Update:</span>
                  <p>{t.messages[t.messages.length - 1].text}</p>
                </div>
              )}
            </div>
          ))
        )}
      </div>

      {/* Create Ticket Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl p-6 w-full max-w-md shadow-2xl animate-in zoom-in-95">
            <h3 className="text-base font-black text-slate-900 mb-1">Create Support Ticket</h3>
            <p className="text-xs text-slate-500 mb-4">
              Describe your issue and operational dispatch supervisors will follow up.
            </p>

            <form onSubmit={handleCreateTicket} className="space-y-3">
              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Issue Category</label>
                <select
                  value={newTicket.category}
                  onChange={(e) => setNewTicket({ ...newTicket, category: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs font-medium"
                >
                  {categories.map((c, i) => (
                    <option key={i} value={c}>{c}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Subject</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Customer not picking up phone"
                  value={newTicket.subject}
                  onChange={(e) => setNewTicket({ ...newTicket, subject: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Booking Ref (Optional)</label>
                <input
                  type="text"
                  placeholder="e.g. JOB-9481"
                  value={newTicket.bookingReference}
                  onChange={(e) => setNewTicket({ ...newTicket, bookingReference: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs font-mono"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Description</label>
                <textarea
                  rows={3}
                  required
                  placeholder="Provide complete context of the situation..."
                  value={newTicket.description}
                  onChange={(e) => setNewTicket({ ...newTicket, description: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs"
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="flex-1 py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-xl text-xs"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="flex-1 py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl text-xs shadow-md shadow-blue-500/20"
                >
                  {submitting ? 'Registering...' : 'Submit Ticket'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Emergency SOS */}
      <SosModal isOpen={sosOpen} onClose={() => setSosOpen(false)} />
    </div>
  );
};
export default SupportTickets;
