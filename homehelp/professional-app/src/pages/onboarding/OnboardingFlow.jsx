import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { kycApi } from '../../api/kycApi';
import { professionalApi } from '../../api/professionalApi';
import { payoutApi } from '../../api/payoutApi';
import { SERVICE_CATEGORIES, SERVICE_AREAS, REQUIRED_DOCUMENTS } from '../../constants/services';
import { WorkerStatus, KycStatus } from '../../constants/status';
import {
  CheckCircle2, ChevronRight, ChevronLeft, UploadCloud,
  FileCheck, Shield, Clock, MapPin, Building, User, Award, AlertCircle
} from 'lucide-react';

export const OnboardingFlow = () => {
  const { user, refreshProfile } = useAuth();
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [submitting, setSubmitting] = useState(false);
  const [submittedSuccess, setSubmittedSuccess] = useState(false);

  // Form states across the 7 steps
  const [basicInfo, setBasicInfo] = useState({
    fullName: user?.fullName || '',
    phone: user?.phone || '',
    email: user?.email || '',
    dob: '1992-06-15',
    profilePhoto: user?.profilePhoto || 'https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=300&auto=format&fit=crop&q=80'
  });

  const [proInfo, setProInfo] = useState({
    serviceCategory: user?.serviceCategory || 'ac-repair',
    skills: user?.skills || ['AC Repair', 'Gas Refill'],
    newSkillInput: '',
    experienceYears: user?.experienceYears || 4,
    languages: ['Telugu', 'Hindi'],
    description: 'Certified home services professional with over 4 years of hands-on experience across Hyderabad.'
  });

  const [selectedAreas, setSelectedAreas] = useState(user?.serviceAreas || ['hyd-madhapur', 'hyd-gachibowli']);

  const [uploadedDocs, setUploadedDocs] = useState({
    aadhaar: { uploaded: true, filename: 'aadhaar_card_front_back.pdf' },
    pan: { uploaded: true, filename: 'pan_card.jpg' },
    bank_proof: { uploaded: true, filename: 'bank_cheque_passbook.jpg' },
    skill_certificate: { uploaded: false, filename: null }
  });

  const [bankInfo, setBankInfo] = useState({
    accountHolder: user?.fullName || 'Rajesh Sharma',
    bankName: 'HDFC Bank Ltd',
    accountNumber: '50100492819284',
    ifsc: 'HDFC0001628'
  });

  const [availability, setAvailability] = useState({
    days: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
    startTime: '08:30',
    endTime: '19:00',
    breakStartTime: '13:00',
    breakEndTime: '14:00'
  });

  const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  const toggleDay = (day) => {
    setAvailability(prev => ({
      ...prev,
      days: prev.days.includes(day) ? prev.days.filter(d => d !== day) : [...prev.days, day]
    }));
  };

  const toggleArea = (areaId) => {
    setSelectedAreas(prev =>
      prev.includes(areaId) ? prev.filter(id => id !== areaId) : [...prev, areaId]
    );
  };

  const addSkill = () => {
    if (proInfo.newSkillInput.trim() && !proInfo.skills.includes(proInfo.newSkillInput.trim())) {
      setProInfo(prev => ({
        ...prev,
        skills: [...prev.skills, prev.newSkillInput.trim()],
        newSkillInput: ''
      }));
    }
  };

  const removeSkill = (skill) => {
    setProInfo(prev => ({
      ...prev,
      skills: prev.skills.filter(s => s !== skill)
    }));
  };

  const handleDocMockUpload = (docId) => {
    setUploadedDocs(prev => ({
      ...prev,
      [docId]: {
        uploaded: true,
        filename: `${docId}_document_${Math.floor(100 + Math.random() * 900)}.pdf`
      }
    }));
  };

  const handleFinalSubmit = async () => {
    setSubmitting(true);
    try {
      // 1. Update basic profile and professional info
      await professionalApi.updateProfile({
        fullName: basicInfo.fullName,
        phone: basicInfo.phone,
        email: basicInfo.email,
        serviceCategory: proInfo.serviceCategory,
        skills: proInfo.skills,
        experienceYears: Number(proInfo.experienceYears),
        languages: proInfo.languages,
        serviceAreas: selectedAreas,
        availability,
        status: WorkerStatus.UNDER_REVIEW // Enforce rule: transitions to UNDER_REVIEW
      });

      // 2. Submit bank details
      await payoutApi.updateBankDetails(bankInfo);

      // 3. Submit KYC documents
      await kycApi.submitKyc({
        documents: Object.keys(uploadedDocs).map(k => ({
          type: k,
          status: 'PENDING_REVIEW',
          filename: uploadedDocs[k].filename
        }))
      });

      await refreshProfile();
      setSubmittedSuccess(true);
    } catch (err) {
      console.error('Submission error:', err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 pb-20">
      {/* Top Banner */}
      <div className="bg-blue-700 text-white p-6 shadow-md">
        <div className="max-w-lg mx-auto">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-bold uppercase tracking-wider text-blue-200">
              Partner Onboarding
            </span>
            <span className="text-xs font-semibold bg-blue-800 px-2.5 py-1 rounded-full text-blue-100">
              Step {currentStep} of 7
            </span>
          </div>
          <h1 className="text-xl font-black">
            {currentStep === 1 && 'Basic Information'}
            {currentStep === 2 && 'Skills & Experience'}
            {currentStep === 3 && 'Service Area Zones'}
            {currentStep === 4 && 'KYC Documents'}
            {currentStep === 5 && 'Bank & Payout Setup'}
            {currentStep === 6 && 'Working Hours & Shifts'}
            {currentStep === 7 && 'Review & Verification'}
          </h1>
          <p className="text-xs text-blue-100 mt-1">
            Complete all steps to get verified by HomeHelp verification team.
          </p>

          {/* Stepper Progress Bar */}
          <div className="w-full bg-blue-900/60 h-1.5 rounded-full mt-4 overflow-hidden">
            <div
              className="bg-emerald-400 h-full transition-all duration-300 rounded-full"
              style={{ width: `${(currentStep / 7) * 100}%` }}
            />
          </div>
        </div>
      </div>

      <div className="max-w-lg mx-auto p-4">
        {submittedSuccess ? (
          <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm text-center py-10 mt-4">
            <div className="w-16 h-16 bg-amber-50 text-amber-600 rounded-full flex items-center justify-center mx-auto mb-4">
              <Clock className="w-9 h-9" />
            </div>
            <h2 className="text-xl font-bold text-slate-900 mb-2">Application Under Review</h2>
            <p className="text-xs text-slate-600 max-w-sm mx-auto mb-6 leading-relaxed">
              Your profile, KYC documents, and bank details have been submitted. Our compliance team in Hyderabad is reviewing your application. You will be notified once approved.
            </p>
            <div className="bg-amber-50 border border-amber-200 rounded-2xl p-4 text-left text-xs text-amber-900 mb-6">
              <span className="font-bold block mb-1">Status: UNDER_REVIEW</span>
              <p>Estimated verification turnaround: 24 to 48 hours. Live job offers unlock immediately upon admin verification.</p>
            </div>
            <button
              onClick={() => navigate('/dashboard')}
              className="w-full bg-blue-600 text-white font-bold py-3.5 rounded-xl shadow-md hover:bg-blue-700 transition"
            >
              Go to Partner Dashboard
            </button>
          </div>
        ) : (
          <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm">
            {/* STEP 1: BASIC INFO */}
            {currentStep === 1 && (
              <div className="space-y-4">
                <div className="flex items-center gap-4 p-3 bg-slate-50 rounded-2xl border border-slate-200">
                  <img
                    src={basicInfo.profilePhoto}
                    alt="Profile"
                    className="w-16 h-16 rounded-2xl object-cover ring-2 ring-blue-500"
                  />
                  <div>
                    <h4 className="text-sm font-bold text-slate-800">Partner Profile Photo</h4>
                    <p className="text-xs text-slate-500 mb-2">Clear face photo without glasses or cap</p>
                    <span className="text-[11px] text-blue-600 font-bold bg-blue-50 px-2 py-0.5 rounded border border-blue-200">
                      Photo Added
                    </span>
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Full Name</label>
                  <input
                    type="text"
                    value={basicInfo.fullName}
                    onChange={(e) => setBasicInfo({ ...basicInfo, fullName: e.target.value })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Phone</label>
                    <input
                      type="tel"
                      value={basicInfo.phone}
                      onChange={(e) => setBasicInfo({ ...basicInfo, phone: e.target.value })}
                      className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Date of Birth</label>
                    <input
                      type="date"
                      value={basicInfo.dob}
                      onChange={(e) => setBasicInfo({ ...basicInfo, dob: e.target.value })}
                      className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Email</label>
                  <input
                    type="email"
                    value={basicInfo.email}
                    onChange={(e) => setBasicInfo({ ...basicInfo, email: e.target.value })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                  />
                </div>
              </div>
            )}

            {/* STEP 2: PROFESSIONAL INFO */}
            {currentStep === 2 && (
              <div className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Category</label>
                  <select
                    value={proInfo.serviceCategory}
                    onChange={(e) => setProInfo({ ...proInfo, serviceCategory: e.target.value })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm font-medium"
                  >
                    {SERVICE_CATEGORIES.map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Specific Skills</label>
                  <div className="flex gap-2 mb-2">
                    <input
                      type="text"
                      placeholder="Add a trade skill..."
                      value={proInfo.newSkillInput}
                      onChange={(e) => setProInfo({ ...proInfo, newSkillInput: e.target.value })}
                      className="flex-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs"
                    />
                    <button
                      type="button"
                      onClick={addSkill}
                      className="px-3 py-2 bg-blue-600 text-white rounded-xl text-xs font-bold"
                    >
                      Add
                    </button>
                  </div>
                  <div className="flex flex-wrap gap-1.5">
                    {proInfo.skills.map(s => (
                      <span
                        key={s}
                        className="bg-blue-50 text-blue-700 border border-blue-200 text-xs px-2.5 py-1 rounded-full flex items-center gap-1.5"
                      >
                        <span>{s}</span>
                        <button
                          type="button"
                          onClick={() => removeSkill(s)}
                          className="text-blue-500 hover:text-blue-800 font-bold"
                        >
                          ×
                        </button>
                      </span>
                    ))}
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Total Experience (Years)</label>
                  <input
                    type="number"
                    min="1"
                    max="40"
                    value={proInfo.experienceYears}
                    onChange={(e) => setProInfo({ ...proInfo, experienceYears: e.target.value })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Professional Bio / Summary</label>
                  <textarea
                    rows={3}
                    value={proInfo.description}
                    onChange={(e) => setProInfo({ ...proInfo, description: e.target.value })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-xs text-slate-800"
                  />
                </div>
              </div>
            )}

            {/* STEP 3: SERVICE AREA */}
            {currentStep === 3 && (
              <div className="space-y-3">
                <p className="text-xs text-slate-600 mb-2">
                  Select zones in Hyderabad where you can commute and accept service bookings.
                </p>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                  {SERVICE_AREAS.map(area => {
                    const isSelected = selectedAreas.includes(area.id);
                    return (
                      <div
                        key={area.id}
                        onClick={() => toggleArea(area.id)}
                        className={`p-3 rounded-2xl border cursor-pointer transition flex items-center justify-between ${
                          isSelected ? 'bg-blue-50 border-blue-400' : 'bg-slate-50 border-slate-200 hover:bg-slate-100'
                        }`}
                      >
                        <div>
                          <h4 className="text-xs font-bold text-slate-800">{area.name}</h4>
                          <span className="text-[10px] text-slate-500">{area.zone} • {area.pincode}</span>
                        </div>
                        <input
                          type="checkbox"
                          checked={isSelected}
                          readOnly
                          className="rounded text-blue-600 focus:ring-0"
                        />
                      </div>
                    );
                  })}
                </div>
              </div>
            )}

            {/* STEP 4: DOCUMENTS */}
            {currentStep === 4 && (
              <div className="space-y-3">
                <p className="text-xs text-slate-600 mb-3">
                  Upload clear photos or PDFs for verification. Encrypted and stored privately.
                </p>

                {REQUIRED_DOCUMENTS.map(doc => {
                  const state = uploadedDocs[doc.id];
                  return (
                    <div
                      key={doc.id}
                      className="p-3.5 rounded-2xl border border-slate-200 bg-slate-50 flex items-center justify-between"
                    >
                      <div className="flex-1 pr-3">
                        <div className="flex items-center gap-1.5">
                          <h4 className="text-xs font-bold text-slate-800">{doc.name}</h4>
                          {doc.required && (
                            <span className="text-[10px] text-red-600 font-semibold">*Required</span>
                          )}
                        </div>
                        <p className="text-[11px] text-slate-500 mt-0.5">{doc.description}</p>
                        {state?.uploaded && (
                          <span className="text-[11px] text-emerald-700 font-medium flex items-center gap-1 mt-1">
                            <FileCheck className="w-3.5 h-3.5" />
                            <span>{state.filename}</span>
                          </span>
                        )}
                      </div>

                      <button
                        type="button"
                        onClick={() => handleDocMockUpload(doc.id)}
                        className={`px-3 py-2 rounded-xl text-xs font-bold flex items-center gap-1.5 transition ${
                          state?.uploaded
                            ? 'bg-emerald-50 text-emerald-700 border border-emerald-300'
                            : 'bg-blue-600 text-white hover:bg-blue-700'
                        }`}
                      >
                        <UploadCloud className="w-3.5 h-3.5" />
                        <span>{state?.uploaded ? 'Re-upload' : 'Upload'}</span>
                      </button>
                    </div>
                  );
                })}
              </div>
            )}

            {/* STEP 5: BANK DETAILS */}
            {currentStep === 5 && (
              <div className="space-y-4">
                <div className="p-3 bg-blue-50 border border-blue-200 rounded-2xl flex items-start gap-2.5 text-xs text-blue-900">
                  <Building className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
                  <p>
                    Earnings are credited directly to this bank account via automated settlement cycles.
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Account Holder Name</label>
                  <input
                    type="text"
                    value={bankInfo.accountHolder}
                    onChange={(e) => setBankInfo({ ...bankInfo, accountHolder: e.target.value })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Bank Name</label>
                  <input
                    type="text"
                    value={bankInfo.bankName}
                    onChange={(e) => setBankInfo({ ...bankInfo, bankName: e.target.value })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Account Number</label>
                  <input
                    type="text"
                    value={bankInfo.accountNumber}
                    onChange={(e) => setBankInfo({ ...bankInfo, accountNumber: e.target.value })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm font-mono"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">IFSC Code</label>
                  <input
                    type="text"
                    value={bankInfo.ifsc}
                    onChange={(e) => setBankInfo({ ...bankInfo, ifsc: e.target.value.toUpperCase() })}
                    className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm font-mono uppercase"
                  />
                </div>
              </div>
            )}

            {/* STEP 6: AVAILABILITY */}
            {currentStep === 6 && (
              <div className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-2">Working Days</label>
                  <div className="grid grid-cols-4 gap-2">
                    {daysOfWeek.map(d => {
                      const active = availability.days.includes(d);
                      return (
                        <button
                          key={d}
                          type="button"
                          onClick={() => toggleDay(d)}
                          className={`p-2.5 rounded-xl text-xs font-bold border transition ${
                            active ? 'bg-blue-600 text-white border-blue-600' : 'bg-slate-50 text-slate-700 border-slate-200'
                          }`}
                        >
                          {d.slice(0, 3)}
                        </button>
                      );
                    })}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Shift Start Time</label>
                    <input
                      type="time"
                      value={availability.startTime}
                      onChange={(e) => setAvailability({ ...availability, startTime: e.target.value })}
                      className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Shift End Time</label>
                    <input
                      type="time"
                      value={availability.endTime}
                      onChange={(e) => setAvailability({ ...availability, endTime: e.target.value })}
                      className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Break Start</label>
                    <input
                      type="time"
                      value={availability.breakStartTime}
                      onChange={(e) => setAvailability({ ...availability, breakStartTime: e.target.value })}
                      className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Break End</label>
                    <input
                      type="time"
                      value={availability.breakEndTime}
                      onChange={(e) => setAvailability({ ...availability, breakEndTime: e.target.value })}
                      className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
                    />
                  </div>
                </div>
              </div>
            )}

            {/* STEP 7: REVIEW & SUBMIT */}
            {currentStep === 7 && (
              <div className="space-y-4">
                <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 text-xs space-y-2.5">
                  <div className="flex justify-between border-b border-slate-200 pb-2">
                    <span className="text-slate-500">Full Name</span>
                    <span className="font-bold text-slate-800">{basicInfo.fullName}</span>
                  </div>
                  <div className="flex justify-between border-b border-slate-200 pb-2">
                    <span className="text-slate-500">Category</span>
                    <span className="font-bold text-slate-800">{proInfo.serviceCategory}</span>
                  </div>
                  <div className="flex justify-between border-b border-slate-200 pb-2">
                    <span className="text-slate-500">Service Zones</span>
                    <span className="font-bold text-slate-800">{selectedAreas.length} Zones Selected</span>
                  </div>
                  <div className="flex justify-between border-b border-slate-200 pb-2">
                    <span className="text-slate-500">Bank Account</span>
                    <span className="font-mono font-bold text-slate-800">
                      {'•••• ' + bankInfo.accountNumber.slice(-4)} ({bankInfo.bankName})
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Mandatory KYC Docs</span>
                    <span className="font-bold text-emerald-600">Aadhaar & PAN Ready</span>
                  </div>
                </div>

                <div className="bg-blue-50 border border-blue-200 p-3.5 rounded-2xl flex items-start gap-2 text-xs text-blue-900">
                  <Shield className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
                  <p>
                    By submitting, you agree to HomeHelp Professional partner guidelines, quality benchmarks, and background verification checks.
                  </p>
                </div>
              </div>
            )}

            {/* Navigation buttons */}
            <div className="flex items-center justify-between gap-3 mt-6 pt-4 border-t border-slate-100">
              {currentStep > 1 ? (
                <button
                  type="button"
                  onClick={() => setCurrentStep(prev => prev - 1)}
                  className="px-4 py-2.5 rounded-xl border border-slate-300 text-slate-700 font-semibold text-xs flex items-center gap-1 hover:bg-slate-50"
                >
                  <ChevronLeft className="w-4 h-4" />
                  <span>Previous</span>
                </button>
              ) : (
                <div />
              )}

              {currentStep < 7 ? (
                <button
                  type="button"
                  onClick={() => setCurrentStep(prev => prev + 1)}
                  className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold flex items-center gap-1 shadow-sm transition"
                >
                  <span>Continue</span>
                  <ChevronRight className="w-4 h-4" />
                </button>
              ) : (
                <button
                  type="button"
                  onClick={handleFinalSubmit}
                  disabled={submitting}
                  className="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 shadow-md shadow-emerald-600/20 transition active:scale-95"
                >
                  <CheckCircle2 className="w-4 h-4" />
                  <span>{submitting ? 'Submitting Application...' : 'Submit for Verification'}</span>
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
export default OnboardingFlow;
