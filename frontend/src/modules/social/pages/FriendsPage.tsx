import { KeyboardEvent, useEffect, useState } from 'react'
import { Button, Card, Input } from '@/shared/components'
import { useAuthStore } from '@/shared/store/auth.store'
import { socialApi } from '../api'
import type { FeedCommentDto, FeedEntryDto, FriendDto, UserSearchDto, WorkoutSummary } from '../types'

const feedConfig: Record<string, { icon: string; label: string; color: string; bg: string }> = {
  WORKOUT_COMPLETED: { icon: '🏋️',  label: 'ukonczyl trening',    color: 'text-emerald-700', bg: 'bg-emerald-50' },
  CHALLENGE_JOINED:  { icon: '🏆',  label: 'dolaczyl do wyzwania', color: 'text-yellow-700',  bg: 'bg-yellow-50'  },
  CHALLENGE_CREATED: { icon: '➕',  label: 'utworzyl wyzwanie',    color: 'text-blue-700',    bg: 'bg-blue-50'    },
}

function parseApiTimestamp(value: string) {
  const hasTimezone = /(?:Z|[+-]\d{2}:?\d{2})$/.test(value)
  return new Date(hasTimezone ? value : `${value}Z`)
}

function timeAgo(iso: string) {
  const diff = Math.max(0, Math.floor((Date.now() - parseApiTimestamp(iso).getTime()) / 1000))
  if (diff < 60) return 'przed chwila'
  if (diff < 3600) return `${Math.floor(diff / 60)} min temu`
  if (diff < 86400) return `${Math.floor(diff / 3600)} godz. temu`
  return `${Math.floor(diff / 86400)} dni temu`
}

function WorkoutDetails({ summary }: { summary: WorkoutSummary }) {
  const exercises = summary.sets.reduce<Record<string, { reps: number[]; weight: number[] }>>(
    (acc, set) => {
      if (!acc[set.exerciseName]) acc[set.exerciseName] = { reps: [], weight: [] }
      acc[set.exerciseName].reps.push(set.reps)
      acc[set.exerciseName].weight.push(set.weightKg)
      return acc
    },
    {}
  )

  return (
    <div className="mx-4 mb-4 overflow-hidden rounded-xl border border-emerald-100 bg-gradient-to-br from-emerald-50 to-white">
      {/* Statystyki */}
      <div className="flex gap-6 border-b border-emerald-100 px-4 py-3">
        <div className="text-center">
          <p className="text-lg font-bold text-emerald-600">{summary.totalSets}</p>
          <p className="text-xs text-gray-400">serii</p>
        </div>
        <div className="text-center">
          <p className="text-lg font-bold text-emerald-600">{summary.totalVolumeKg.toFixed(0)}</p>
          <p className="text-xs text-gray-400">kg wolumenu</p>
        </div>
        <div className="text-center">
          <p className="text-lg font-bold text-emerald-600">{Object.keys(exercises).length}</p>
          <p className="text-xs text-gray-400">cwiczen</p>
        </div>
      </div>
      {/* Lista cwiczen */}
      <ul className="divide-y divide-emerald-50">
        {Object.entries(exercises).map(([name, data]) => (
          <li key={name} className="flex items-center justify-between px-4 py-2">
            <span className="text-sm font-medium text-gray-800">{name}</span>
            <div className="flex gap-1">
              {data.reps.map((r, i) => (
                <span key={i} className="rounded-full bg-emerald-100 px-2 py-0.5 text-xs font-medium text-emerald-700">
                  {r}×{data.weight[i] > 0 ? `${data.weight[i]}kg` : 'bw'}
                </span>
              ))}
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}

function FeedCard({ entry, currentUserId }: { entry: FeedEntryDto; currentUserId: number | null }) {
  const [comments, setComments] = useState<FeedCommentDto[]>(entry.comments ?? [])
  const [commentInput, setCommentInput] = useState('')
  const [sending, setSending] = useState(false)
  const [showComments, setShowComments] = useState(false)

  const handleAddComment = async () => {
    const text = commentInput.trim()
    if (!text) return
    setSending(true)
    try {
      const newComment = await socialApi.addComment(entry.id, text)
      setComments((prev) => [...prev, newComment])
      setCommentInput('')
      setShowComments(true)
    } finally {
      setSending(false)
    }
  }

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') void handleAddComment()
  }

  const cfg = feedConfig[entry.type] ?? { icon: '📌', label: entry.type, color: 'text-gray-700', bg: 'bg-gray-50' }

  return (
    <div className="overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm transition-shadow hover:shadow-md">
      {/* Kolorowy pasek na górze zależny od typu */}
      <div className={`h-1 w-full ${entry.type === 'WORKOUT_COMPLETED' ? 'bg-emerald-400' : entry.type === 'CHALLENGE_JOINED' ? 'bg-yellow-400' : 'bg-blue-400'}`} />

      {/* Header */}
      <div className="flex items-center justify-between px-5 pt-4">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-primary to-emerald-400 text-sm font-bold text-white shadow-sm">
            {entry.username[0].toUpperCase()}
          </div>
          <div>
            <p className="text-sm font-bold text-gray-900">{entry.username}</p>
            <p className="text-xs text-gray-400">{timeAgo(entry.createdAt)}</p>
          </div>
        </div>
        {/* Badge typu */}
        <span className={`flex items-center gap-1 rounded-full px-3 py-1 text-xs font-semibold ${cfg.bg} ${cfg.color}`}>
          {cfg.icon} {cfg.label}
          {entry.challengeTitle && <span className="ml-1 font-bold">„{entry.challengeTitle}"</span>}
        </span>
      </div>

      {/* Szczegoly treningu */}
      <div className="px-5 py-4">
        {entry.workoutSummary && <WorkoutDetails summary={entry.workoutSummary} />}
      </div>

      {/* Pasek akcji */}
      <div className="flex items-center gap-4 border-t border-gray-100 px-5 py-2">
        <button
          className={`flex items-center gap-1.5 text-xs font-semibold transition-colors ${showComments ? 'text-primary' : 'text-gray-400 hover:text-primary'}`}
          onClick={() => setShowComments((v) => !v)}
        >
          <span>💬</span>
          <span>{comments.length > 0 ? `${comments.length} komentarzy` : 'Skomentuj'}</span>
        </button>
      </div>

      {/* Komentarze */}
      {showComments && (
        <div className="border-t border-gray-100 bg-gray-50 px-5 pb-4 pt-3">
          {comments.length > 0 && (
            <ul className="mb-3 space-y-2">
              {comments.map((c) => (
                <li key={c.id} className="flex items-start gap-2">
                  <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-primary text-xs font-bold text-white">
                    {c.username[0].toUpperCase()}
                  </div>
                  <div className="flex-1">
                    <div className="rounded-2xl rounded-tl-none bg-white px-3 py-2 shadow-sm">
                      <p className="text-xs font-bold text-gray-700">{c.username}</p>
                      <p className="text-sm text-gray-800">{c.content}</p>
                    </div>
                    <p className="mt-0.5 pl-1 text-xs text-gray-400">{timeAgo(c.createdAt)}</p>
                  </div>
                </li>
              ))}
            </ul>
          )}

          {currentUserId !== null && (
            <div className="mt-2 flex gap-2">
              <Input
                placeholder="Napisz komentarz..."
                value={commentInput}
                onChange={(e) => setCommentInput(e.target.value)}
                onKeyDown={handleKeyDown}
                className="flex-1 rounded-full text-sm"
              />
              <Button
                variant="secondary"
                className="rounded-full text-xs"
                loading={sending}
                onClick={() => void handleAddComment()}
              >
                Wyslij
              </Button>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

// Właściciel: Rafał
export function FriendsPage() {
  const currentUser = useAuthStore((s) => s.user)
  const [friends, setFriends] = useState<FriendDto[]>([])
  const [pending, setPending] = useState<FriendDto[]>([])
  const [feed, setFeed] = useState<FeedEntryDto[]>([])
  const [searchQuery, setSearchQuery] = useState('')
  const [searchResults, setSearchResults] = useState<UserSearchDto[]>([])
  const [searching, setSearching] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadData = async () => {
    setError('')
    setLoading(true)
    try {
      const [friendsList, pendingList, feedList] = await Promise.all([
        socialApi.getFriends(),
        socialApi.getPendingRequests(),
        socialApi.getFeed(),
      ])
      setFriends(friendsList)
      setPending(pendingList)
      setFeed(feedList)
    } catch {
      setError('Nie udalo sie pobrac danych.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadData()
  }, [])

  const handleSearch = async () => {
    if (!searchQuery.trim()) return
    setSearching(true)
    try {
      const results = await socialApi.searchUsers(searchQuery.trim())
      setSearchResults(results)
    } catch {
      setError('Nie udalo sie wyszukac uzytkownikow.')
    } finally {
      setSearching(false)
    }
  }

  const handleSendRequest = async (addresseeId: number) => {
    try {
      await socialApi.sendFriendRequest(addresseeId)
      setSearchResults((prev) => prev.filter((u) => u.id !== addresseeId))
    } catch {
      setError('Nie udalo sie wyslac zaproszenia.')
    }
  }

  const handleAccept = async (friendshipId: number) => {
    try {
      await socialApi.acceptFriendRequest(friendshipId)
      await loadData()
    } catch {
      setError('Nie udalo sie zaakceptowac zaproszenia.')
    }
  }

  const handleReject = async (friendshipId: number) => {
    try {
      await socialApi.rejectFriendRequest(friendshipId)
      setPending((prev) => prev.filter((p) => p.friendshipId !== friendshipId))
    } catch {
      setError('Nie udalo sie odrzucic zaproszenia.')
    }
  }

  const handleRemoveFriend = async (friendshipId: number) => {
    try {
      await socialApi.removeFriend(friendshipId)
      setFriends((prev) => prev.filter((f) => f.friendshipId !== friendshipId))
    } catch {
      setError('Nie udalo sie usunac znajomego.')
    }
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Znajomi</h1>
        <p className="mt-1 text-sm text-gray-500">Zarzadzaj znajomymi i sledz ich aktywnosc.</p>
      </div>

      {error && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 gap-5 xl:grid-cols-[360px_1fr]">
        <div className="space-y-5">

          {/* Wyszukiwanie */}
          <Card title="Dodaj znajomego">
            <div className="flex gap-2">
              <Input
                placeholder="Wyszukaj po nazwie..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && void handleSearch()}
                className="flex-1"
              />
              <Button onClick={() => void handleSearch()} loading={searching} variant="secondary">
                Szukaj
              </Button>
            </div>

            {searchResults.length > 0 && (
              <ul className="mt-3 space-y-2">
                {searchResults.map((user) => (
                  <li key={user.id} className="flex items-center justify-between rounded-lg border border-gray-100 px-3 py-2">
                    <div>
                      <p className="text-sm font-medium text-gray-900">{user.username}</p>
                      <p className="text-xs text-gray-500">{user.email}</p>
                    </div>
                    <Button variant="secondary" className="text-xs" onClick={() => void handleSendRequest(user.id)}>
                      Zapros
                    </Button>
                  </li>
                ))}
              </ul>
            )}
          </Card>

          {/* Oczekujace zaproszenia */}
          {pending.length > 0 && (
            <Card title={`Zaproszenia (${pending.length})`}>
              <ul className="space-y-2">
                {pending.map((req) => (
                  <li key={req.friendshipId} className="flex items-center justify-between rounded-lg border border-gray-100 px-3 py-2">
                    <div>
                      <p className="text-sm font-medium text-gray-900">{req.username}</p>
                      <p className="text-xs text-gray-500">{req.email}</p>
                    </div>
                    <div className="flex gap-2">
                      <Button className="text-xs" onClick={() => void handleAccept(req.friendshipId)}>
                        Akceptuj
                      </Button>
                      <Button variant="ghost" className="text-xs" onClick={() => void handleReject(req.friendshipId)}>
                        Odrzuc
                      </Button>
                    </div>
                  </li>
                ))}
              </ul>
            </Card>
          )}

          {/* Lista znajomych */}
          <Card title={`Znajomi (${friends.length})`}>
            {loading ? (
              <p className="text-sm text-gray-500">Ladowanie...</p>
            ) : friends.length === 0 ? (
              <p className="text-sm text-gray-500">Nie masz jeszcze znajomych.</p>
            ) : (
              <ul className="space-y-2">
                {friends.map((friend) => (
                  <li key={friend.friendshipId} className="flex items-center gap-3 rounded-lg border border-gray-100 px-3 py-2">
                    <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-primary text-sm font-bold text-white">
                      {friend.username[0].toUpperCase()}
                    </div>
                    <div className="flex-1">
                      <p className="text-sm font-medium text-gray-900">{friend.username}</p>
                      <p className="text-xs text-gray-500">{friend.email}</p>
                    </div>
                    <button
                      className="text-xs text-gray-400 hover:text-red-500"
                      onClick={() => void handleRemoveFriend(friend.friendshipId)}
                    >
                      Usun
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </Card>
        </div>

        {/* Feed aktywnosci */}
        <div>
          <h2 className="mb-4 text-base font-semibold text-gray-900">Aktywnosc znajomych</h2>
          {loading ? (
            <p className="text-sm text-gray-500">Ladowanie...</p>
          ) : feed.length === 0 ? (
            <div className="rounded-xl border border-gray-200 bg-white px-6 py-10 text-center text-sm text-gray-500 shadow-sm">
              Brak aktywnosci do wyswietlenia.
            </div>
          ) : (
            <div className="space-y-4">
              {feed.map((entry) => (
                <FeedCard key={entry.id} entry={entry} currentUserId={currentUser?.id ?? null} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
