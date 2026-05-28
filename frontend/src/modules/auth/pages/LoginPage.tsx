import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Button, Input } from '@/shared/components'
import { useAuthStore } from '@/shared/store/auth.store'
import { apiClient } from '@/shared/api/client'
import type { AuthResponse, LoginRequest } from '@/shared/types'

export function LoginPage() {
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const [form, setForm] = useState<LoginRequest>({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await apiClient.post<AuthResponse>('/auth/login', form)
      setAuth(data.user, data.token)
      navigate('/dashboard')
    } catch {
      setError('Nieprawidłowy email lub hasło')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-surface">
      <div className="w-full max-w-sm rounded-2xl border border-gray-200 bg-white p-8 shadow-sm">
        <h1 className="mb-1 text-2xl font-bold text-gray-900">Witaj z powrotem</h1>
        <p className="mb-6 text-sm text-gray-500">Zaloguj się do FORMA</p>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
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
            Zaloguj się
          </Button>
        </form>

        <p className="mt-5 text-center text-sm text-gray-500">
          Nie masz konta?{' '}
          <Link to="/register" className="font-medium text-primary hover:underline">
            Zarejestruj się
          </Link>
        </p>

        {import.meta.env.DEV && (
          <div className="mt-6 border-t border-dashed border-gray-200 pt-4">
            <p className="mb-2 text-center text-xs text-gray-400">tylko w trybie deweloperskim</p>
            <Button
              variant="secondary"
              className="w-full"
              onClick={() => {
                setAuth({ id: 1, username: 'rafal_dev', email: 'rafal@forma.dev' }, 'mock-token-dev')
                navigate('/dashboard')
              }}
            >
              Wejdź bez logowania (DEV)
            </Button>
          </div>
        )}
      </div>
    </div>
  )
}
