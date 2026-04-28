import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
})

// Attach token to every request if present
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export const register = (email, password) =>
  api.post('/auth/register', { email, password })

export const login = (email, password) =>
  api.post('/auth/login', { email, password })

export const verifyOtp = (email, otp) =>
  api.post('/auth/verify-otp', { email, otp })

export default api
