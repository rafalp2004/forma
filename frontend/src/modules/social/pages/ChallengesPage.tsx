import { FormEvent, useEffect, useState } from 'react'
import { Button, Card, Input } from '@/shared/components'
import { socialApi } from '../api'
import type { ChallengeCreateDto, ChallengeDto, LeaderboardEntryDto } from '../types'

const metricLabel: Record<string, string> = {
  TOTAL_VOLUME: 'Laczny wolumen (kg)',
  WORKOUT_COUNT: 'Liczba treningow',
  STREAK_DAYS: 'Seria dni',
}

const statusLabel: Record<string, string> = {
  ACTIVE: 'Aktywne',
  FINISHED: 'Zakonczone',
  PENDING: 'Oczekujace',
}

const statusColor: Record<string, string> = {
  ACTIVE: 'bg-green-100 text-green-700',
  FINISHED: 'bg-gray-100 text-gray-500',
  PENDING: 'bg-yellow-100 text-yellow-700',
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('pl-PL', { day: 'numeric', month: 'short', year: 'numeric' })
}

const emptyForm: ChallengeCreateDto = {
  title: '',
  description: '',
  startDate: '',
  endDate: '',
  metric: 'TOTAL_VOLUME',
}

// Właściciel: Rafał
export function ChallengesPage() {
  const [challenges, setChallenges] = useState<ChallengeDto[]>([])
  const [selectedChallenge, setSelectedChallenge] = useState<ChallengeDto | null>(null)
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntryDto[]>([])
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState<ChallengeCreateDto>(emptyForm)
  const [loading, setLoading] = useState(true)
  const [loadingLeaderboard, setLoadingLeaderboard] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const loadChallenges = async () => {
    setError('')
    setLoading(true)
    try {
      const data = await socialApi.getActiveChallenges()
      setChallenges(data)
    } catch {
      setError('Nie udalo sie pobrac wyzwan.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadChallenges()
  }, [])

  const handleSelect = async (challenge: ChallengeDto) => {
    setSelectedChallenge(challenge)
    setLoadingLeaderboard(true)
    try {
      const data = await socialApi.getLeaderboard(challenge.id)
      setLeaderboard(data)
    } catch {
      setLeaderboard([])
    } finally {
      setLoadingLeaderboard(false)
    }
  }

  const handleJoin = async (id: number) => {
    try {
      await socialApi.joinChallenge(id)
      await loadChallenges()
    } catch {
      setError('Nie udalo sie dolaczyc do wyzwania.')
    }
  }

  const handleLeave = async (id: number) => {
    try {
      await socialApi.leaveChallenge(id)
      await loadChallenges()
    } catch {
      setError('Nie udalo sie opuscic wyzwania.')
    }
  }

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    if (!form.title || !form.startDate || !form.endDate) {
      setError('Wypelnij wszystkie wymagane pola.')
      return
    }
    setSaving(true)
    try {
      await socialApi.createChallenge(form)
      setForm(emptyForm)
      setShowForm(false)
      await loadChallenges()
    } catch {
      setError('Nie udalo sie utworzyc wyzwania.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Wyzwania</h1>
          <p className="mt-1 text-sm text-gray-500">Aktywne wyzwania i ranking uczestnikow.</p>
        </div>
        <Button onClick={() => setShowForm((v) => !v)}>
          {showForm ? 'Anuluj' : '+ Nowe wyzwanie'}
        </Button>
      </div>

      {error && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {/* Formularz tworzenia */}
      {showForm && (
        <Card title="Nowe wyzwanie" className="mb-5">
          <form onSubmit={(e) => void handleCreate(e)} className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <Input
              label="Nazwa *"
              value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
              placeholder="Np. Maj - wyzwanie wolumenu"
            />
            <div className="flex flex-col gap-1">
              <label className="text-sm font-medium text-gray-700">Metryka *</label>
              <select
                value={form.metric}
                onChange={(e) => setForm((f) => ({ ...f, metric: e.target.value }))}
                className="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
              >
                {Object.entries(metricLabel).map(([value, label]) => (
                  <option key={value} value={value}>{label}</option>
                ))}
              </select>
            </div>
            <Input
              label="Data startu *"
              type="date"
              value={form.startDate}
              onChange={(e) => setForm((f) => ({ ...f, startDate: e.target.value }))}
            />
            <Input
              label="Data konca *"
              type="date"
              value={form.endDate}
              onChange={(e) => setForm((f) => ({ ...f, endDate: e.target.value }))}
            />
            <div className="sm:col-span-2">
              <Input
                label="Opis"
                value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                placeholder="Opcjonalny opis wyzwania"
              />
            </div>
            <div className="sm:col-span-2">
              <Button type="submit" loading={saving}>
                Utworz wyzwanie
              </Button>
            </div>
          </form>
        </Card>
      )}

      <div className="grid grid-cols-1 gap-5 xl:grid-cols-[1fr_360px]">
        {/* Lista wyzwan */}
        <div className="space-y-3">
          {loading ? (
            <p className="text-sm text-gray-500">Ladowanie wyzwan...</p>
          ) : challenges.length === 0 ? (
            <Card>
              <p className="text-sm text-gray-500">Brak aktywnych wyzwan. Utworz pierwsze!</p>
            </Card>
          ) : (
            challenges.map((challenge) => (
              <div
                key={challenge.id}
                onClick={() => void handleSelect(challenge)}
                className={`cursor-pointer rounded-xl border bg-white p-4 shadow-sm transition-colors hover:border-primary ${
                  selectedChallenge?.id === challenge.id ? 'border-primary ring-2 ring-primary/20' : 'border-gray-200'
                }`}
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <h3 className="font-semibold text-gray-900">{challenge.title}</h3>
                      <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${statusColor[challenge.status] ?? 'bg-gray-100 text-gray-500'}`}>
                        {statusLabel[challenge.status] ?? challenge.status}
                      </span>
                    </div>
                    {challenge.description && (
                      <p className="mt-1 text-sm text-gray-500">{challenge.description}</p>
                    )}
                    <div className="mt-2 flex flex-wrap gap-3 text-xs text-gray-400">
                      <span>📊 {metricLabel[challenge.metric] ?? challenge.metric}</span>
                      <span>📅 {formatDate(challenge.startDate)} – {formatDate(challenge.endDate)}</span>
                      <span>👥 {challenge.participantCount} uczestnikow</span>
                    </div>
                  </div>
                  {challenge.isParticipant ? (
                    <div className="flex shrink-0 flex-col items-end gap-1">
                      <span className="rounded-full bg-green-100 px-3 py-1 text-xs font-medium text-green-700">
                        ✓ Uczestniczysz
                      </span>
                      <button
                        className="text-xs text-gray-400 hover:text-red-500"
                        onClick={(e) => { e.stopPropagation(); void handleLeave(challenge.id) }}
                      >
                        Opusc
                      </button>
                    </div>
                  ) : (
                    <Button
                      variant="secondary"
                      className="shrink-0 text-xs"
                      onClick={(e) => { e.stopPropagation(); void handleJoin(challenge.id) }}
                    >
                      Dolacz
                    </Button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>

        {/* Ranking */}
        <Card title={selectedChallenge ? `Ranking: ${selectedChallenge.title}` : 'Ranking'}>
          {!selectedChallenge ? (
            <p className="text-sm text-gray-500">Wybierz wyzwanie aby zobaczyc ranking.</p>
          ) : loadingLeaderboard ? (
            <p className="text-sm text-gray-500">Ladowanie rankingu...</p>
          ) : leaderboard.length === 0 ? (
            <p className="text-sm text-gray-500">Brak uczestnikow w tym wyzwaniu.</p>
          ) : (
            <ol className="space-y-2">
              {leaderboard.map((entry) => (
                <li key={entry.userId} className="flex items-center gap-3 rounded-lg border border-gray-100 px-3 py-2">
                  <span className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-sm font-bold ${
                    entry.rank === 1 ? 'bg-yellow-100 text-yellow-700' :
                    entry.rank === 2 ? 'bg-gray-100 text-gray-600' :
                    entry.rank === 3 ? 'bg-orange-100 text-orange-600' :
                    'bg-gray-50 text-gray-400'
                  }`}>
                    {entry.rank}
                  </span>
                  <span className="flex-1 text-sm font-medium text-gray-900">{entry.username}</span>
                  <span className="text-sm font-semibold text-primary">{entry.score.toFixed(1)}</span>
                </li>
              ))}
            </ol>
          )}
        </Card>
      </div>
    </div>
  )
}
