import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#22C55E',
          hover: '#16A34A',
          light: '#DCFCE7',
        },
        sidebar: '#111827',
        surface: '#F9FAFB',
      },
      width: {
        sidebar: '240px',
      },
    },
  },
  plugins: [],
} satisfies Config
