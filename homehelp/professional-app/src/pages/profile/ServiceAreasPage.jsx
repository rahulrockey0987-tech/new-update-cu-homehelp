import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { professionalApi } from '../../api/professionalApi';
import { SERVICE_AREAS } from '../../constants/services';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { ArrowLeft, MapPin, Check, Save } from 'lucide-react';

export const ServiceAreasPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [selectedAreas, setSelectedAreas] = useState([]);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true);
        const res = await professionalApi.getProfile();
        if (res.success && res.data?.serviceAreas) {
          setSelectedAreas(res.data.serviceAreas);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const toggleArea = (id) => {
    setSelectedAreas(prev =>
      prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
    );
  };

  const handleSave = async () => {
    setSaving(true);
    setSuccess(false);
    try {
      const res = await professionalApi.updateServiceAreas(selectedAreas);
      if (res.success) {
        setSuccess(true);
        setTimeout(() => setSuccess(false), 3000);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading service areas..." />;

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
      </div>

      <div>
        <h1 className="text-xl font-black text-slate-900 tracking-tight">Service Zones</h1>
        <p className="text-xs text-slate-500">Select regions in Hyderabad where you can fulfill bookings</p>
      </div>

      {success && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-2xl text-xs font-semibold flex items-center gap-2">
          <Check className="w-4 h-4 text-emerald-600" />
          <span>Service zones updated! You will only be dispatched to selected zones.</span>
        </div>
      )}

      <div className="space-y-2">
        {SERVICE_AREAS.map(area => {
          const isSelected = selectedAreas.includes(area.id);
          return (
            <div
              key={area.id}
              onClick={() => toggleArea(area.id)}
              className={`p-3.5 rounded-2xl border cursor-pointer transition flex items-center justify-between ${
                isSelected
                  ? 'bg-blue-50 border-blue-400 shadow-sm'
                  : 'bg-white border-slate-200 hover:bg-slate-50'
              }`}
            >
              <div className="flex items-center gap-3">
                <div className={`w-8 h-8 rounded-xl flex items-center justify-center ${
                  isSelected ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-500'
                }`}>
                  <MapPin className="w-4 h-4" />
                </div>
                <div>
                  <h4 className="text-xs font-bold text-slate-800">{area.name}</h4>
                  <span className="text-[11px] text-slate-500">{area.zone} Zone • PIN {area.pincode}</span>
                </div>
              </div>

              <input
                type="checkbox"
                checked={isSelected}
                readOnly
                className="w-4 h-4 rounded text-blue-600 focus:ring-0"
              />
            </div>
          );
        })}
      </div>

      <button
        onClick={handleSave}
        disabled={saving}
        className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-2xl text-xs font-bold flex items-center justify-center gap-2 shadow-md shadow-blue-500/20 transition active:scale-95"
      >
        <Save className="w-4 h-4" />
        <span>{saving ? 'Saving...' : `Save ${selectedAreas.length} Selected Zones`}</span>
      </button>
    </div>
  );
};
export default ServiceAreasPage;
