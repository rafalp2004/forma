import { useEffect, useMemo, useState } from 'react'
import { apiClient } from '@/shared/api/client'
import { Button, Card, Input } from '@/shared/components'
import type { ExerciseDto } from '@/shared/types'

export function ExercisesPage() {
  const [exercises, setExercises] = useState<ExerciseDto[]>([])
  const [selectedExercise, setSelectedExercise] = useState<ExerciseDto | null>(null)
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [syncing, setSyncing] = useState(false)
  const [error, setError] = useState('')
  const [syncMessage, setSyncMessage] = useState('')

  const muscleGroups = useMemo(() => {
    const groups = new Set(exercises.map((exercise) => exercise.muscleGroup).filter(Boolean))
    return Array.from(groups).sort((a, b) => a.localeCompare(b))
  }, [exercises])

  const loadExercises = async (query = search) => {
    setError('')
    setLoading(true)

    try {
      const { data } = await apiClient.get<ExerciseDto[]>('/exercises', {
        params: query.trim() ? { search: query.trim() } : undefined,
      })
      setExercises(data)
      setSelectedExercise((current) => {
        if (!current) return null
        return data.find((exercise) => exercise.id === current.id) ?? null
      })
    } catch {
      setError('Nie udalo sie pobrac cwiczen.')
    } finally {
      setLoading(false)
    }
  }

  const handleSync = async () => {
    setSyncing(true)
    setSyncMessage('')
    setError('')

    try {
      const { data } = await apiClient.post<string>('/exercises/sync')
      setSyncMessage(data)
      await loadExercises('')
      setSearch('')
    } catch {
      setError('Synchronizacja cwiczen nie powiodla sie.')
    } finally {
      setSyncing(false)
    }
  }

  useEffect(() => {
    void loadExercises('')
  }, [])

  return (
    <div>
      <div className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Baza cwiczen</h1>
          <p className="mt-2 text-sm text-gray-500">
            Gotowy katalog ruchow do budowania treningow, filtrowania po partiach i szybkiego
            sprawdzania techniki.
          </p>
        </div>

        <Button type="button" onClick={handleSync} loading={syncing}>
          Synchronizuj baze
        </Button>
      </div>

      <div className="mb-6 grid grid-cols-1 gap-4 lg:grid-cols-[minmax(0,1fr)_220px]">
        <form
          className="flex flex-col gap-3 sm:flex-row"
          onSubmit={(event) => {
            event.preventDefault()
            void loadExercises()
          }}
        >
          <Input
            label="Szukaj po nazwie lub partii"
            placeholder="np. bench, legs, chest"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            className="min-w-0 sm:w-80"
          />
          <div className="flex items-end gap-2">
            <Button type="submit">Szukaj</Button>
            <Button
              type="button"
              variant="secondary"
              onClick={() => {
                setSearch('')
                void loadExercises('')
              }}
            >
              Reset
            </Button>
          </div>
        </form>

        <Card title="Partie miesniowe" className="p-4">
          <p className="text-2xl font-bold text-primary">{muscleGroups.length}</p>
          <p className="mt-1 text-xs text-gray-500">w aktualnym wyniku</p>
        </Card>
      </div>

      {error && (
        <div className="mb-4 rounded-lg border border-red-100 bg-red-50 p-3 text-sm text-red-600">
          {error}
        </div>
      )}

      {syncMessage && (
        <div className="mb-4 rounded-lg border border-green-100 bg-green-50 p-3 text-sm text-green-700">
          {syncMessage}
        </div>
      )}

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
        <Card>
          {loading ? (
            <p className="text-sm text-gray-500">Ladowanie cwiczen...</p>
          ) : exercises.length === 0 ? (
            <div className="rounded-lg border border-dashed border-gray-200 p-8 text-center">
              <p className="font-medium text-gray-900">Brak cwiczen</p>
              <p className="mt-1 text-sm text-gray-500">
                Zmien filtr albo zsynchronizuj baze cwiczen.
              </p>
            </div>
          ) : (
            <div className="overflow-hidden rounded-lg border border-gray-200">
              <table className="min-w-full divide-y divide-gray-200 text-sm">
                <thead className="bg-gray-50 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                  <tr>
                    <th className="px-4 py-3">Cwiczenie</th>
                    <th className="px-4 py-3">Partia</th>
                    <th className="px-4 py-3">Sprzet</th>
                    <th className="px-4 py-3 text-right">Akcja</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 bg-white">
                  {exercises.map((exercise) => (
                    <tr key={exercise.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3">
                        <p className="font-medium text-gray-900">{exercise.name}</p>
                        <p className="mt-0.5 text-xs text-gray-500">ID: {exercise.id}</p>
                      </td>
                      <td className="px-4 py-3 text-gray-700">{exercise.muscleGroup || '-'}</td>
                      <td className="px-4 py-3 text-gray-700">{exercise.equipment || '-'}</td>
                      <td className="px-4 py-3 text-right">
                        <Button
                          type="button"
                          variant="ghost"
                          onClick={() => setSelectedExercise(exercise)}
                          className="px-3"
                        >
                          Szczegoly
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>

        <Card title="Szczegoly cwiczenia">
          {selectedExercise ? (
            <div className="space-y-4">
              {selectedExercise.gifUrl && (
                <div className="overflow-hidden rounded-lg border border-gray-200 bg-gray-50">
                  <img
                    src={selectedExercise.gifUrl}
                    alt={selectedExercise.name}
                    className="h-52 w-full object-contain"
                  />
                </div>
              )}

              <div>
                <h2 className="text-lg font-semibold text-gray-900">{selectedExercise.name}</h2>
                <p className="mt-1 text-xs text-gray-500">Stale ExerciseDB ID: {selectedExercise.id}</p>
              </div>

              <dl className="grid grid-cols-2 gap-3 text-sm">
                <div className="rounded-lg bg-gray-50 p-3">
                  <dt className="text-xs font-medium uppercase text-gray-500">Partia</dt>
                  <dd className="mt-1 text-gray-900">{selectedExercise.muscleGroup || '-'}</dd>
                </div>
                <div className="rounded-lg bg-gray-50 p-3">
                  <dt className="text-xs font-medium uppercase text-gray-500">Sprzet</dt>
                  <dd className="mt-1 text-gray-900">{selectedExercise.equipment || '-'}</dd>
                </div>
              </dl>

              {selectedExercise.instructions && (
                <div>
                  <p className="mb-1 text-sm font-medium text-gray-900">Instrukcja</p>
                  <p className="whitespace-pre-line text-sm leading-6 text-gray-600">
                    {selectedExercise.instructions}
                  </p>
                </div>
              )}
            </div>
          ) : (
            <p className="text-sm text-gray-500">Wybierz cwiczenie z listy, aby zobaczyc szczegoly.</p>
          )}
        </Card>
      </div>
    </div>
  )
}
