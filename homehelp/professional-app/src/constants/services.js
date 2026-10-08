export const SERVICE_CATEGORIES = [
  { id: 'ac-repair', name: 'AC & Appliance Repair', icon: 'Wrench', description: 'Air conditioner servicing, gas refilling, and repairs' },
  { id: 'cleaning', name: 'Home Deep Cleaning', icon: 'Sparkles', description: 'Deep kitchen, bathroom, sofa, and full house cleaning' },
  { id: 'electrician', name: 'Electrical Work', icon: 'Zap', description: 'Wiring, switchboard, inverter, and appliance installation' },
  { id: 'plumbing', name: 'Plumbing Solutions', icon: 'Droplets', description: 'Pipe leakages, taps, bath fittings, and motor repairs' },
  { id: 'painting', name: 'Painting & Waterproofing', icon: 'Paintbrush', description: 'Interior/exterior painting, texture, and damp proofing' },
  { id: 'pest-control', name: 'Pest Control', icon: 'ShieldAlert', description: 'Cockroach, termite, bed bug, and mosquito management' },
  { id: 'carpentry', name: 'Carpentry & Furniture', icon: 'Hammer', description: 'Furniture repair, assembly, door lock, and woodwork' }
];

export const SERVICE_AREAS = [
  { id: 'hyd-madhapur', name: 'Madhapur', zone: 'Cyberabad', pincode: '500081' },
  { id: 'hyd-gachibowli', name: 'Gachibowli', zone: 'Cyberabad', pincode: '500032' },
  { id: 'hyd-hitec', name: 'Hitec City', zone: 'Cyberabad', pincode: '500081' },
  { id: 'hyd-kukatpally', name: 'Kukatpally', zone: 'North West', pincode: '500072' },
  { id: 'hyd-kondapur', name: 'Kondapur', zone: 'Cyberabad', pincode: '500084' },
  { id: 'hyd-banjara', name: 'Banjara Hills', zone: 'Central', pincode: '500034' },
  { id: 'hyd-jubilee', name: 'Jubilee Hills', zone: 'Central', pincode: '500033' },
  { id: 'hyd-secunderabad', name: 'Secunderabad', zone: 'North', pincode: '500003' },
  { id: 'hyd-manikonda', name: 'Manikonda', zone: 'West', pincode: '500089' },
  { id: 'hyd-miyapur', name: 'Miyapur', zone: 'North West', pincode: '500049' }
];

export const REQUIRED_DOCUMENTS = [
  { id: 'aadhaar', name: 'Aadhaar Card (Front & Back)', required: true, description: 'Government issued unique identity card' },
  { id: 'pan', name: 'PAN Card', required: true, description: 'Tax identification card for payout processing' },
  { id: 'police_clearance', name: 'Police Verification / Background Certificate', required: false, description: 'Boosts your trust score and job allocation priority' },
  { id: 'skill_certificate', name: 'Trade / Skill Certification', required: false, description: 'ITI, NSDC or manufacturer certification' },
  { id: 'bank_proof', name: 'Bank Passbook / Cancelled Cheque', required: true, description: 'To verify account holder name and IFSC' }
];
