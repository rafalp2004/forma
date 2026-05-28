import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { UserDto } from '../types'

interface AuthState {
  user: UserDto | null
  token: string | null
  setAuth: (user: UserDto, token: string) => void
  logout: () => void
  isAuthenticated: () => boolean
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,

      setAuth: (user, token) => {
        localStorage.setItem('forma_token', token)
        set({ user, token })
      },

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
