import { WorkerStatus, KycStatus, JobStatus, PayoutStatus } from '../constants/status';

const STORAGE_KEYS = {
  USER: 'homehelp_pro_user',
  TOKEN: 'homehelp_pro_token',
  JOBS: 'homehelp_pro_jobs',
  KYC: 'homehelp_pro_kyc',
  EARNINGS: 'homehelp_pro_earnings',
  PAYOUTS: 'homehelp_pro_payouts',
  NOTIFICATIONS: 'homehelp_pro_notifications',
  SUPPORT: 'homehelp_pro_support',
  BANK: 'homehelp_pro_bank'
};

const initialUser = {
  id: 'pro_882194',
  fullName: 'Rajesh Sharma',
  email: 'rajesh.sharma@homehelp.pro',
  phone: '+91 98765 43210',
  city: 'Hyderabad',
  serviceCategory: 'ac-repair',
  skills: ['Inverter AC Servicing', 'Compressor Replacement', 'Gas Leak Detection', 'Copper Piping'],
  experienceYears: 6,
  languages: ['Telugu', 'Hindi', 'English'],
  status: WorkerStatus.ACTIVE,
  kycStatus: KycStatus.VERIFIED,
  isOnline: true,
  rating: 4.88,
  totalJobsCompleted: 142,
  acceptanceRate: 96,
  serviceAreas: ['hyd-madhapur', 'hyd-gachibowli', 'hyd-kondapur', 'hyd-hitec'],
  profilePhoto: 'https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=300&auto=format&fit=crop&q=80',
  availability: {
    days: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
    startTime: '08:30',
    endTime: '19:00',
    breakStartTime: '13:00',
    breakEndTime: '14:00',
    isAutoAcceptEligible: true
  }
};

const initialBank = {
  accountHolder: 'Rajesh Sharma',
  bankName: 'HDFC Bank Ltd',
  accountNumber: '50100492819284',
  maskedAccount: 'XXXXXXXX9284',
  ifsc: 'HDFC0001628',
  isVerified: true
};

const initialJobs = [
  {
    id: 'JOB-PARTNER-150',
    customerName: 'Rahul Sharma',
    customerPhone: '+91 98765 43210',
    serviceTitle: 'Hire a Partner (General Assistance)',
    category: 'general_partner',
    locality: 'Madhapur, Silicon Valley Colony, Flat 402',
    landmark: 'Near Cyber Towers & Metro Stn',
    distanceKm: 1.2,
    scheduledDate: new Date().toISOString().split('T')[0],
    scheduledTime: 'Immediate (Next 15 mins)',
    estimatedDuration: '3 hrs',
    ratePerHour: 150,
    requiredHours: 3,
    earnings: 450,
    grossPrice: 450,
    status: JobStatus.OFFERED,
    otp: '5821',
    hasVoiceNote: true,
    voiceDuration: '14s',
    specialInstructions: 'Customer Voice Note attached: Need assistance moving boxes from room to garage.',
    coordinates: { lat: 17.4486, lng: 78.3908 },
    checklist: [
      { id: 'c1', label: 'Listen to customer voice note', completed: false },
      { id: 'c2', label: 'Arrive at Madhapur premises', completed: false },
      { id: 'c3', label: 'Verify 4-digit customer arrival OTP', completed: false },
      { id: 'c4', label: 'Complete 3-hour assistance task', completed: false }
    ],
    proofPhotos: [],
    additionalCharges: []
  },
  {
    id: 'JOB-9481',
    customerName: 'Ananya Reddy',
    customerPhone: '+91 98490 12345',
    serviceTitle: 'Split AC Master Servicing & Deep Foam Wash',
    category: 'ac-repair',
    locality: 'Madhapur, Silicon Valley Colony, Flat 402, Green Heights',
    landmark: 'Near Durgam Cheruvu Metro Station',
    distanceKm: 2.3,
    scheduledDate: new Date().toISOString().split('T')[0],
    scheduledTime: '11:30 AM',
    estimatedDuration: '1.5 hrs',
    earnings: 850,
    grossPrice: 1050,
    status: JobStatus.ASSIGNED,
    otp: '4829',
    specialInstructions: 'Customer mentions slight cooling drop in bedroom unit. Please inspect condenser coil.',
    coordinates: { lat: 17.4483, lng: 78.3915 },
    checklist: [
      { id: 'c1', label: 'Inspect outdoor fan & compressor current', completed: false },
      { id: 'c2', label: 'Foam jet wash cooling fins & drain tray', completed: false },
      { id: 'c3', label: 'Check gas pressure (PSI reading)', completed: false },
      { id: 'c4', label: 'Temperature drop verification across grill', completed: false }
    ],
    proofPhotos: [],
    additionalCharges: []
  },
  {
    id: 'JOB-9482',
    customerName: 'Vikram Mehta',
    customerPhone: '+91 99887 76655',
    serviceTitle: 'AC Gas Top-up & Leakage Fix (R32)',
    category: 'ac-repair',
    locality: 'Kondapur, Raghavendra Colony, Villa 18',
    landmark: 'Behind Botanical Gardens',
    distanceKm: 4.8,
    scheduledDate: new Date().toISOString().split('T')[0],
    scheduledTime: '03:00 PM',
    estimatedDuration: '2 hrs',
    earnings: 1200,
    grossPrice: 1550,
    status: JobStatus.OFFERED,
    otp: '7391',
    specialInstructions: 'Unit makes hissing noise when turned on.',
    coordinates: { lat: 17.4622, lng: 78.3568 },
    checklist: [],
    proofPhotos: [],
    additionalCharges: []
  },
  {
    id: 'JOB-9478',
    customerName: 'Suresh Rao',
    customerPhone: '+91 91234 56789',
    serviceTitle: 'Comprehensive 2-Unit AC Maintenance',
    category: 'ac-repair',
    locality: 'Gachibowli, Telecom Nagar',
    landmark: 'Near DLF Cybercity Gate 2',
    distanceKm: 3.1,
    scheduledDate: new Date(Date.now() - 86400000).toISOString().split('T')[0],
    scheduledTime: '10:00 AM',
    estimatedDuration: '2.5 hrs',
    earnings: 1650,
    grossPrice: 2100,
    status: JobStatus.COMPLETED,
    completedAt: new Date(Date.now() - 80000000).toISOString(),
    ratingGiven: 5,
    otp: '1192',
    specialInstructions: 'Smooth service completed.',
    proofPhotos: ['https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=400&auto=format&fit=crop&q=80'],
    additionalCharges: []
  }
];

const initialNotifications = [
  {
    id: 'notif-1',
    title: 'New Service Request',
    message: 'New AC Gas Top-up request in Kondapur (4.8 km). Accept within 10 mins.',
    type: 'job_offer',
    jobId: 'JOB-9482',
    timestamp: new Date(Date.now() - 15 * 60000).toISOString(),
    read: false
  },
  {
    id: 'notif-2',
    title: 'Payout Dispatched',
    message: '₹4,820 was transferred to your verified HDFC account ending in 9284.',
    type: 'payout',
    timestamp: new Date(Date.now() - 24 * 3600000).toISOString(),
    read: true
  },
  {
    id: 'notif-3',
    title: 'Profile Verified',
    message: 'Your documents and Aadhaar e-KYC have been validated. You are now receiving live bookings.',
    type: 'verification',
    timestamp: new Date(Date.now() - 48 * 3600000).toISOString(),
    read: true
  }
];

export const getStored = (key, defaultVal) => {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : defaultVal;
  } catch {
    return defaultVal;
  }
};

export const setStored = (key, value) => {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    console.error('Storage write error', e);
  }
};

export const initStorage = () => {
  if (!localStorage.getItem(STORAGE_KEYS.USER)) {
    setStored(STORAGE_KEYS.USER, initialUser);
    setStored(STORAGE_KEYS.TOKEN, 'jwt_token_sample_worker_verified_prod');
  }
  if (!localStorage.getItem(STORAGE_KEYS.JOBS)) {
    setStored(STORAGE_KEYS.JOBS, initialJobs);
  }
  if (!localStorage.getItem(STORAGE_KEYS.BANK)) {
    setStored(STORAGE_KEYS.BANK, initialBank);
  }
  if (!localStorage.getItem(STORAGE_KEYS.NOTIFICATIONS)) {
    setStored(STORAGE_KEYS.NOTIFICATIONS, initialNotifications);
  }
};

export { STORAGE_KEYS };
