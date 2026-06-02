import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Button, Input } from '@/shared/components'
import { useAuthStore } from '@/shared/store/auth.store'
import { apiClient } from '@/shared/api/client'
import type { AuthResponse, LoginRequest, UserDetailsResponse } from '@/shared/types'
import { AxiosError } from 'axios'

export function LoginPage() {
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const setUser = useAuthStore((s) => s.setUser)
  const [form, setForm] = useState({ identifier: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      // Backend expects 'username' property
      const loginData: LoginRequest = { 
        username: form.identifier, 
        password: form.password 
      }
      console.log('Sending login data:', loginData)
      const { data } = await apiClient.post<AuthResponse>('/auth/login', loginData)
      console.log('Login response data:', data)
      
      // Save token
      setAuth(data.token)

      // Fetch user details to populate store
      console.log('Fetching user profile...')
      const { data: profile } = await apiClient.get<UserDetailsResponse>('/users/me')
      console.log('Profile data:', profile)
      setUser({
        id: 0,
        username: profile.username,
        email: profile.email
      })

      navigate('/dashboard')
    } catch (err: unknown) {
      if (err instanceof AxiosError) {
        console.error('Login error detail:', err.response?.data || err.message)
      } else {
        console.error('Login error:', err)
      }
      setError('Nieprawidłowa nazwa użytkownika lub hasło')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen bg-white">
      {/* Left Pane - Branding */}
      <div className="hidden lg:flex lg:w-1/2 bg-primary items-center justify-center p-12 text-white relative overflow-hidden">
        {/* Decorative circles */}
        <div className="absolute top-[-10%] right-[-10%] w-64 h-64 bg-white/10 rounded-full" />
        <div className="absolute bottom-[-10%] left-[-10%] w-64 h-64 bg-white/10 rounded-full" />
        
        <div className="max-w-md relative z-10">
          <h1 className="text-7xl font-black tracking-tighter mb-8">FORMA</h1>
          <div className="space-y-4 text-xl font-medium opacity-90">
            <p>Twój trening.</p>
            <p>Twoje postępy.</p>
            <p>Twoja społeczność.</p>
          </div>
        </div>
      </div>

      {/* Right Pane - Login Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8">
        <div className="w-full max-w-md">
          <div className="mb-10 text-center lg:text-left">
            <h2 className="text-3xl font-bold text-gray-900 mb-2">Witaj z powrotem!</h2>
            <p className="text-gray-500">Zaloguj się na swoje konto</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <Input
              label="Nazwa użytkownika"
              placeholder="jan_kowalski"
              value={form.identifier}
              onChange={(e) => setForm((f) => ({ ...f, identifier: e.target.value }))}
              required
              className="h-12"
            />
            <div className="space-y-1">
              <Input
                label="Hasło"
                type="password"
                placeholder="••••••••"
                value={form.password}
                onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
                required
                className="h-12"
              />
              <div className="flex justify-end">
                <button type="button" className="text-sm text-primary hover:underline font-medium">
                  Zapomniałeś hasła?
                </button>
              </div>
            </div>

            {error && (
              <div className="p-3 bg-red-50 border border-red-100 rounded-lg text-sm text-red-600">
                {error}
              </div>
            )}

            <Button type="submit" loading={loading} className="w-full h-12 text-base font-semibold">
              Zaloguj się
            </Button>

            <div className="relative py-4">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-200"></div>
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="bg-white px-2 text-gray-400">lub</span>
              </div>
            </div>

            <p className="text-center text-sm text-gray-500">
              Nie masz konta?{' '}
              <Link to="/register" className="font-bold text-primary hover:underline">
                Zarejestruj się
              </Link>
            </p>
          </form>
          
          {import.meta.env.DEV && (
            <div className="mt-12 pt-6 border-t border-dashed border-gray-200">
              <Button
                variant="secondary"
                className="w-full h-10 text-xs"
                onClick={() => {
                  setAuth('mock-token-dev')
                  setUser({ id: 1, username: 'rafal_dev', email: 'rafal@forma.dev' })
                  navigate('/dashboard')
                }}
              >
                AUTO-LOGIN (DEV)
              </Button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
