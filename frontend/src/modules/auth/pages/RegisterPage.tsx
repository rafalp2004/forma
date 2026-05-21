import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Button, Input } from '@/shared/components'
import { useAuthStore } from '@/shared/store/auth.store'
import { apiClient } from '@/shared/api/client'
import type { AuthResponse, RegisterRequest } from '@/shared/types'

export function RegisterPage() {
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const [form, setForm] = useState<RegisterRequest>({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await apiClient.post<AuthResponse>('/auth/register', form)
      setAuth(data.user, data.token)
      navigate('/dashboard')
    } catch {
      setError('Rejestracja nie powiodła się. Spróbuj ponownie.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-surface">
      <div className="w-full max-w-sm rounded-2xl border border-gray-200 bg-white p-8 shadow-sm">
        <h1 className="mb-1 text-2xl font-bold text-gray-900">Utwórz konto</h1>
        <p className="mb-6 text-sm text-gray-500">Dołącz do FORMA</p>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <Input
            label="Nazwa użytkownika"
            placeholder="jan_kowalski"
            value={form.username}
            onChange={(e) => setForm((f) => ({ ...f, username: e.target.value }))}
            required
          />
          <Input
            label="Email"
            type="email"
            placeholder="jan@example.com"
            value={form.email}
            onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
            required
          />
          <Input
            label="Hasło"
            type="password"
            placeholder="••••••••"
            value={form.password}
            onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
            required
          />
          {error && <p className="text-sm text-red-500">{error}</p>}
          <Button type="submit" loading={loading} className="mt-2 w-full">
            Zarejestruj się
          </Button>
        </form>

        <p className="mt-5 text-center text-sm text-gray-500">
          Masz już konto?{' '}
          <Link to="/login" className="font-medium text-primary hover:underline">
            Zaloguj się
          </Link>
        </p>
      </div>
    </div>
  )
}
