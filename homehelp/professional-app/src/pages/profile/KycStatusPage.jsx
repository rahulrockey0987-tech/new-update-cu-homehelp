import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { kycApi } from '../../api/kycApi';
import StatusBadge from '../../components/common/StatusBadge';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { KycStatus } from '../../constants/status';
import {
  ShieldCheck, FileCheck, ArrowLeft, UploadCloud, AlertCircle,
  Clock, CheckCircle2, ChevronRight, XCircle
} from 'lucide-react';

export const KycStatusPage = () => {
  const [kyc, setKyc] = useState(null);
  const [loading, setLoading] = useState(true);
  const [resubmitting, setResubmitting] = useState(false);
  const navigate = useNavigate();

  const fetchKyc = async () => {
    try {
      setLoading(true);
      const res = await kycApi.getKycStatus();
      if (res.success) {
        setKyc(res.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchKyc();
  }, []);

  const handleResubmit = async () => {
    setResubmitting(true);
    try {
      await kycApi.submitKyc({
        documents: [
          { type: 'aadhaar', filename: 'aadhaar_updated_scan.pdf' },
          { type: 'pan', filename: 'pan_updated_scan.pdf' }
        ]
      });
      await fetchKyc();
    } catch (e) {
      console.error(e);
    } finally {
      setResubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner message="Checking KYC compliance status..." />;

  const isVerified = kyc?.status === KycStatus.VERIFIED;
  const isUnderReview = kyc?.status === KycStatus.UNDER_REVIEW || kyc?.status === KycStatus.SUBMITTED;
  const isRejected = kyc?.status === KycStatus.REJECTED;

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
        <StatusBadge status={kyc?.status} type="kyc" size="md" />
      </div>

      <div>
        <h1 className="text-xl font-black text-slate-900 tracking-tight">KYC & Verification</h1>
        <p className="text-xs text-slate-500">Government identity and legal compliance records</p>
      </div>

      {/* Main Status Hero */}
      <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm text-center">
        {isVerified && (
          <>
            <div className="w-14 h-14 bg-emerald-50 text-emerald-600 rounded-2xl flex items-center justify-center mx-auto mb-3">
              <ShieldCheck className="w-8 h-8" />
            </div>
            <h3 className="text-base font-black text-slate-900">Partner Verified</h3>
            <p className="text-xs text-slate-500 max-w-xs mx-auto mt-1">
              Your government ID, address, and credentials have been verified by HomeHelp Hyderabad.
            </p>
          </>
        )}

        {isUnderReview && (
          <>
            <div className="w-14 h-14 bg-amber-50 text-amber-600 rounded-2xl flex items-center justify-center mx-auto mb-3">
              <Clock className="w-8 h-8" />
            </div>
            <h3 className="text-base font-black text-slate-900">Documents Under Review</h3>
            <p className="text-xs text-slate-500 max-w-xs mx-auto mt-1">
              Our verification compliance team is processing your documents. Turnaround time is 24-48 hours.
            </p>
          </>
        )}

        {isRejected && (
          <>
            <div className="w-14 h-14 bg-rose-50 text-rose-600 rounded-2xl flex items-center justify-center mx-auto mb-3">
              <XCircle className="w-8 h-8" />
            </div>
            <h3 className="text-base font-black text-slate-900">Resubmission Needed</h3>
            <p className="text-xs text-rose-600 max-w-xs mx-auto mt-1">
              {kyc.reviewNotes || 'Document image was blurry or mismatched with name.'}
            </p>
          </>
        )}
      </div>

      {/* Submitted Documents Checklist */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm space-y-3">
        <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500">
          Submitted Verification Proofs
        </h3>

        <div className="space-y-2">
          {kyc?.documents?.map((doc, idx) => (
            <div
              key={idx}
              className="p-3 bg-slate-50 rounded-2xl border border-slate-200 flex items-center justify-between text-xs"
            >
              <div className="flex items-center gap-2.5">
                <FileCheck className="w-4 h-4 text-blue-600 flex-shrink-0" />
                <div>
                  <span className="font-bold text-slate-800 uppercase block">{doc.type}</span>
                  <span className="text-[11px] text-slate-500 font-mono">
                    {doc.number || 'Verified Document Scan'}
                  </span>
                </div>
              </div>
              <span className="text-[11px] font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                {doc.status}
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* Re-upload / Correction button if needed */}
      {!isVerified && (
        <button
          onClick={handleResubmit}
          disabled={resubmitting}
          className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-2xl text-xs font-bold flex items-center justify-center gap-2 shadow-md shadow-blue-500/20 transition active:scale-95"
        >
          <UploadCloud className="w-4 h-4" />
          <span>{resubmitting ? 'Submitting...' : 'Upload Revised Documents'}</span>
        </button>
      )}
    </div>
  );
};
export default KycStatusPage;
