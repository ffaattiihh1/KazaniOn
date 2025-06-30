import React, { useState } from 'react';
import { Box, Button, TextField, Typography, Paper, Link as MuiLink, Alert } from '@mui/material';
// Firebase authentication removed for KVKK compliance - using simple admin auth

const Login = ({ onLogin }: { onLogin: () => void }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  // Simple admin authentication for KVKK compliance
  const ADMIN_CREDENTIALS = {
    email: 'admin@kazanion.com',
    password: 'admin123'
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    // Simple admin check
    if (email === ADMIN_CREDENTIALS.email && password === ADMIN_CREDENTIALS.password) {
      // Store login session
      localStorage.setItem('kazanion-admin-logged-in', 'true');
      localStorage.setItem('kazanion-admin-user', email);
      onLogin();
    } else {
      setError('Geçersiz e-posta veya şifre');
    }
  };

  const handleTestLogin = () => {
    setEmail(ADMIN_CREDENTIALS.email);
    setPassword(ADMIN_CREDENTIALS.password);
  };

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: '#eaf1fb', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Paper elevation={4} sx={{ p: 5, borderRadius: 3, minWidth: 350, maxWidth: 400, width: '100%', textAlign: 'center', bgcolor: '#fff' }}>
        {/* Logo placeholder */}
        <Box sx={{ mb: 3 }}>
          <Box sx={{ width: 80, height: 80, bgcolor: '#1976d2', borderRadius: '50%', mx: 'auto', mb: 1 }} />
          <Typography variant="h5" fontWeight={700} color="#1976d2">KazaniOn Admin</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            PostgreSQL Admin Panel - KVKK Uyumlu
          </Typography>
        </Box>
        
        <form onSubmit={handleLogin}>
          <TextField
            label="E-posta"
            variant="outlined"
            fullWidth
            sx={{ mb: 2 }}
            value={email}
            onChange={e => setEmail(e.target.value)}
            placeholder="admin@kazanion.com"
          />
          <TextField
            label="Şifre"
            type="password"
            variant="outlined"
            fullWidth
            sx={{ mb: 2 }}
            value={password}
            onChange={e => setPassword(e.target.value)}
            placeholder="admin123"
          />
          {error && <Typography color="error" sx={{ mb: 2 }}>{error}</Typography>}
          
          <Button
            type="submit"
            variant="contained"
            fullWidth
            size="large"
            sx={{ bgcolor: '#1976d2', fontWeight: 700, fontSize: 18, py: 1.5, mt: 1, mb: 2, ':hover': { bgcolor: '#115293' } }}
          >
            GİRİŞ YAP
          </Button>
          
          <Button
            variant="outlined"
            fullWidth
            size="large"
            sx={{ fontWeight: 700, fontSize: 16, py: 1, mb: 2, borderColor: '#1976d2', color: '#1976d2', ':hover': { bgcolor: '#eaf1fb', borderColor: '#115293', color: '#115293' } }}
            onClick={handleTestLogin}
          >
            Admin Bilgilerini Doldur
          </Button>

          <Alert severity="info" sx={{ mt: 2, textAlign: 'left' }}>
            <strong>Admin Giriş:</strong><br/>
            E-posta: admin@kazanion.com<br/>
            Şifre: admin123
          </Alert>
        </form>
      </Paper>
    </Box>
  );
};

export default Login; 