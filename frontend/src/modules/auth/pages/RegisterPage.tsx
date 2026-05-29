import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Button, Input } from '@/shared/components'
import { useAuthStore } from '@/shared/store/auth.store'
import { apiClient } from '@/shared/api/client'
import type { AuthResponse, RegisterRequest, UserDetailsResponse } from '@/shared/types'

export function RegisterPage() {
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const setUser = useAuthStore((s) => s.setUser)
  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    
    if (form.password.length < 6) {
      setError('Hasło musi mieć co najmniej 6 znaków')
      return
    }

    if (form.password !== form.confirmPassword) {
      setError('Hasła nie są identyczne')
      return
    }

    setLoading(true)
    try {
      const registerData: RegisterRequest = {
        username: form.username,
        email: form.email,
        password: form.password
      }
      const { data } = await apiClient.post<AuthResponse>('/auth/register', registerData)
      
      // Save token
      setAuth(data.token)

      // Fetch user details to populate store
      const { data: profile } = await apiClient.get<UserDetailsResponse>('/users/me')
      setUser({
        id: 0,
        username: profile.username,
        email: profile.email
      })

      navigate('/dashboard')
    } catch (err: any) {
      const status = err.response?.status
      const message = err.response?.data?.message

      if (status === 409 || message?.includes('already taken')) {
        if (message?.toLowerCase().includes('email')) {
          setError('Ten adres e-mail jest już zajęty')
        } else if (message?.toLowerCase().includes('username')) {
          setError('Ta nazwa użytkownika jest już zajęta')
        } else {
          setError('Użytkownik o podanych danych już istnieje')
        }
      } else {
        setError('Rejestracja nie powiodła się. Spróbuj ponownie.')
      }
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
        
        <div className="max-w-md relative z-10 text-center">
          <h1 className="text-7xl font-black tracking-tighter mb-8">FORMA</h1>
          <p className="text-2xl font-medium opacity-90 leading-relaxed">
            Zacznij swoją przygodę ze zdrowym trybem życia.
          </p>
        </div>
      </div>

      {/* Right Pane - Register Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 overflow-y-auto">
        <div className="w-full max-w-md my-8">
          <div className="mb-8 text-center lg:text-left">
            <h2 className="text-3xl font-bold text-gray-900 mb-2">Utwórz konto</h2>
            <p className="text-gray-500 text-sm">Dołącz do społeczności FORMA</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            <Input
              label="Nazwa użytkownika"
              placeholder="jan_kowalski"
              value={form.username}
              onChange={(e) => setForm((f) => ({ ...f, username: e.target.value }))}
              required
              className="h-11"
            />
            <Input
              label="Adres e-mail"
              type="email"
              placeholder="jan@example.com"
              value={form.email}
              onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
              required
              className="h-11"
            />
            <Input
              label="Hasło"
              type="password"
              placeholder="Minimum 6 znaków"
              value={form.password}
              onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
              required
              className="h-11"
            />
            <Input
              label="Powtórz hasło"
              type="password"
              placeholder="Powtórz hasło"
              value={form.confirmPassword}
              onChange={(e) => setForm((f) => ({ ...f, confirmPassword: e.target.value }))}
              required
              className="h-11"
            />

            {error && (
              <div className="p-3 bg-red-50 border border-red-100 rounded-lg text-sm text-red-600">
                {error}
              </div>
            )}

            <Button type="submit" loading={loading} className="w-full h-11 text-base font-semibold mt-4">
              Utwórz konto
            </Button>

            <p className="text-center text-sm text-gray-500 pt-4">
              Masz już konto?{' '}
              <Link to="/login" className="font-bold text-primary hover:underline">
                Zaloguj się
              </Link>
            </p>
          </form>
        </div>
      </div>
    </div>
  )
}
