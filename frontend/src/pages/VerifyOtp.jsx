import { useState, useRef, useEffect } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import AuthCard from '../components/AuthCard'
import { verifyOtp } from '../api/auth'

export default function VerifyOtp() {
  const navigate = useNavigate()
  const location = useLocation()
  const email = location.state?.email || ''

  const [digits, setDigits] = useState(Array(6).fill(''))
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const inputs = useRef([])

  useEffect(() => {
    inputs.current[0]?.focus()
  }, [])

  const handleChange = (i, value) => {
    if (!/^\d*$/.test(value)) return
    const next = [...digits]
    next[i] = value.slice(-1)
    setDigits(next)
    if (value && i < 5) inputs.current[i + 1]?.focus()
  }

  const handleKeyDown = (i, e) => {
    if (e.key === 'Backspace' && !digits[i] && i > 0) {
      inputs.current[i - 1]?.focus()
    }
  }

  const handlePaste = (e) => {
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
    if (pasted.length === 6) {
      setDigits(pasted.split(''))
      inputs.current[5]?.focus()
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const otp = digits.join('')
    if (otp.length < 6) return setError('Enter all 6 digits')
    setError('')
    setLoading(true)
    try {
      const res = await verifyOtp(email, otp)
      localStorage.setItem('token', res.data.token)
      navigate('/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid OTP. Try again.')
      setDigits(Array(6).fill(''))
      inputs.current[0]?.focus()
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthCard>
      <h2 className="text-lg font-medium text-gray-900 mb-1">Verify your email</h2>
      <p className="text-sm text-gray-500 mb-1">
        Enter the 6-digit code sent to
      </p>
      <p className="text-sm font-medium text-gray-700 mb-6">{email || 'your email'}</p>

      <form onSubmit={handleSubmit}>
        <div className="flex gap-2 mb-4" onPaste={handlePaste}>
          {digits.map((d, i) => (
            <input
              key={i}
              ref={(el) => (inputs.current[i] = el)}
              type="text"
              inputMode="numeric"
              maxLength={1}
              value={d}
              onChange={(e) => handleChange(i, e.target.value)}
              onKeyDown={(e) => handleKeyDown(i, e)}
              className="w-12  h-12 text-center text-lg font-semibold rounded-lg border border-gray-200 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-violet-400"
            />
          ))}
        </div>

        {error && (
          <p className="text-sm text-red-500 bg-red-50 px-3 py-2 rounded-lg mb-4">{error}</p>
        )}

        <button
          type="submit"
          disabled={loading || digits.some((d) => !d)}
          className="w-full h-10 bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium rounded-lg transition-colors"
        >
          {loading ? 'Verifying…' : 'Verify & continue'}
        </button>
      </form>

      <p className="text-sm text-center text-gray-500 mt-5">
        Wrong email?{' '}
        <Link to="/register" className="text-violet-600 hover:underline font-medium">
          Go back
        </Link>
      </p>
    </AuthCard>
  )
}
