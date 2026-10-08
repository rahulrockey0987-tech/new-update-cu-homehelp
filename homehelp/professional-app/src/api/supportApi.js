import apiClient from './axios';
import { STORAGE_KEYS, getStored, setStored } from '../services/mockStorage';
import { TicketStatus } from '../constants/status';

const USE_FALLBACK = import.meta.env.VITE_ENABLE_MOCK_FALLBACK === 'true';

export const supportApi = {
  async getTickets() {
    try {
      return await apiClient.get('/professional/support/tickets');
    } catch (err) {
      if (USE_FALLBACK) {
        return {
          success: true,
          data: getStored(STORAGE_KEYS.SUPPORT, [
            {
              id: 'TCK-4819',
              category: 'Payment / Payout',
              subject: 'Payout settlement timing for weekend jobs',
              status: TicketStatus.RESOLVED,
              createdAt: '2026-09-25T14:20:00Z',
              updatedAt: '2026-09-26T10:15:00Z',
              messages: [
                { sender: 'worker', text: 'When does the Saturday batch get credited?', time: '14:20' },
                { sender: 'agent', text: 'Weekend batches are processed on Monday morning by 11:00 AM.', time: '16:00' }
              ]
            }
          ])
        };
      }
      throw err;
    }
  },

  async createTicket(ticketData) {
    try {
      return await apiClient.post('/professional/support/tickets', ticketData);
    } catch (err) {
      if (USE_FALLBACK) {
        const tickets = getStored(STORAGE_KEYS.SUPPORT, []);
        const newTicket = {
          id: 'TCK-' + Math.floor(1000 + Math.random() * 9000),
          category: ticketData.category,
          subject: ticketData.subject,
          bookingReference: ticketData.bookingReference || null,
          status: TicketStatus.OPEN,
          createdAt: new Date().toISOString(),
          messages: [
            { sender: 'worker', text: ticketData.description, time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) }
          ]
        };
        tickets.unshift(newTicket);
        setStored(STORAGE_KEYS.SUPPORT, tickets);
        return {
          success: true,
          message: 'Support ticket registered. Response expected within 2 hours.',
          data: newTicket
        };
      }
      throw err;
    }
  },

  async triggerSos(sosData) {
    try {
      return await apiClient.post('/professional/safety/sos', sosData);
    } catch (err) {
      if (USE_FALLBACK) {
        console.warn('[SAFETY SOS DISPATCHED]', sosData);
        return {
          success: true,
          message: 'Emergency SOS protocol activated. Dedicated safety response coordinator alerted.',
          data: {
            incidentId: 'INC-SOS-' + Math.floor(100000 + Math.random() * 900000),
            dispatchedAt: new Date().toISOString(),
            safetyHelpline: '+91 1800 200 4499'
          }
        };
      }
      throw err;
    }
  }
};
