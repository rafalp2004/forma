import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { UserDto } from '../types'

interface AuthState {
  user: UserDto | null
  token: string | null
  setAuth: (token: string, user?: UserDto) => void
  setUser: (user: UserDto) => void
  logout: () => void
  isAuthenticated: () => boolean
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,

      setAuth: (token, user) => {
        localStorage.setItem('forma_token', token)
        set({ token, ...(user ? { user } : {}) })
      },

      setUser: (user) => set({ user }),

      logout: () => {
        localStorage.removeItem('forma_token')
        set({ user: null, token: null })
      },

      isAuthenticated: () => get().token !== null,
    }),
    {
      name: 'forma_auth',
      partialize: (state) => ({ user: state.user, token: state.token }),
    }
  )
)
