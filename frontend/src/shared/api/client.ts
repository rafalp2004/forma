import axios from 'axios'

export const apiClient = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Dołącz JWT token do każdego żądania
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('forma_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 401 → wyloguj i przekieruj na login
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('forma_token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
