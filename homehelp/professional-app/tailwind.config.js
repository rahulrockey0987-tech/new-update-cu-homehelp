/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#eef6ff',
          100: '#d9ebff',
          200: '#bcdbff',
          300: '#8ec2ff',
          400: '#589eff',
          500: '#2b7fff',
          600: '#1565ef',
          700: '#0f4ec5',
          800: '#11419f',
          900: '#14387d',
        },
        worker: {
          online: '#10b981',
          busy: '#f59e0b',
          offline: '#6b7280',
          alert: '#ef4444'
        }
      }
    },
  },
  plugins: [],
}
