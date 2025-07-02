import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Card,
  CardContent,
  TextField,
  Button,
  Grid,
  Box,
  Alert,
  Chip,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  CircularProgress
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import SendIcon from '@mui/icons-material/Send';
import GroupIcon from '@mui/icons-material/Group';
import PersonIcon from '@mui/icons-material/Person';

interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fcmToken?: string;
}

const NotificationManagement = () => {
  const [title, setTitle] = useState('');
  const [message, setMessage] = useState('');
  const [selectedUser, setSelectedUser] = useState<number | 'all'>('all');
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<{ success: boolean; message: string } | null>(null);
  const [usersLoading, setUsersLoading] = useState(true);

  // API Base URL - Production/Development otomatik geçiş
  const API_BASE = process.env.NODE_ENV === 'production' 
    ? 'https://kazanion.onrender.com/api' 
    : 'http://localhost:8081/api';

  // Kullanıcıları yükle
  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await fetch(`${API_BASE}/users`);
      const userData = await response.json();
      setUsers(userData);
    } catch (error) {
      console.error('Kullanıcılar yüklenemedi:', error);
    } finally {
      setUsersLoading(false);
    }
  };

  const handleSendNotification = async () => {
    if (!title.trim() || !message.trim()) {
      setResult({ success: false, message: 'Başlık ve mesaj gerekli!' });
      return;
    }

    setLoading(true);
    setResult(null);

    try {
      console.log('Sending notification:', {
        title: title.trim(),
        message: message.trim(),
        userId: selectedUser === 'all' ? null : selectedUser
      });

      const response = await fetch(`${API_BASE}/notifications/send`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          title: title.trim(),
          message: message.trim(),
          userId: selectedUser === 'all' ? null : selectedUser
        }),
      });

      console.log('Response status:', response.status);
      const data = await response.json();
      console.log('Response data:', data);
      setResult(data);

      if (data.success) {
        setTitle('');
        setMessage('');
      }
    } catch (error) {
      setResult({ success: false, message: 'Bağlantı hatası!' });
    } finally {
      setLoading(false);
    }
  };

  const handleTestNotification = async () => {
    setLoading(true);
    setResult(null);

    try {
      console.log('Sending test notification...');

      const response = await fetch(`${API_BASE}/notifications/test`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      console.log('Test response status:', response.status);
      const data = await response.json();
      console.log('Test response data:', data);
      setResult(data);
    } catch (error) {
      console.error('Test notification error:', error);
      setResult({ success: false, message: 'Test bildirimi gönderilemedi!' });
    } finally {
      setLoading(false);
    }
  };

  const getUserDisplayName = (user: User) => {
    if (user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`;
    }
    return user.username;
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
          <NotificationsIcon fontSize="large" color="primary" />
          Bildirim Yönetimi
        </Typography>
        <Typography variant="body1" color="textSecondary">
          Kullanıcılara push notification gönderme sistemi
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Bildirim Gönderme Formu */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 1 }}>
                <SendIcon />
                Bildirim Gönder
              </Typography>

              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Bildirim Başlığı"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="Örn: Yeni Anket Mevcut!"
                    variant="outlined"
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Mesaj"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="Bildirim mesajınızı yazın..."
                    variant="outlined"
                    multiline
                    rows={4}
                  />
                </Grid>

                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Alıcı Seçin</InputLabel>
                    <Select
                      value={selectedUser}
                      onChange={(e) => setSelectedUser(e.target.value as number | 'all')}
                      label="Alıcı Seçin"
                    >
                      <MenuItem value="all">
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <GroupIcon fontSize="small" />
                          Tüm Kullanıcılar
                        </Box>
                      </MenuItem>
                      {users.map((user) => (
                        <MenuItem key={user.id} value={user.id}>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <PersonIcon fontSize="small" />
                            {getUserDisplayName(user)} ({user.email})
                          </Box>
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12}>
                  <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={handleSendNotification}
                      disabled={loading}
                      startIcon={loading ? <CircularProgress size={20} /> : <SendIcon />}
                      sx={{ flex: 1 }}
                    >
                      {loading ? 'Gönderiliyor...' : 'Bildirim Gönder'}
                    </Button>
                    
                    <Button
                      variant="outlined"
                      color="secondary"
                      onClick={handleTestNotification}
                      disabled={loading}
                      sx={{ minWidth: 150 }}
                    >
                      Test Bildirimi
                    </Button>
                  </Box>
                </Grid>

                {result && (
                  <Grid item xs={12}>
                    <Alert severity={result.success ? 'success' : 'error'}>
                      {result.message}
                    </Alert>
                  </Grid>
                )}
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Kullanıcı Listesi */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Kayıtlı Kullanıcılar ({users.length})
              </Typography>
              
              {usersLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
                  <CircularProgress />
                </Box>
              ) : (
                <Box sx={{ maxHeight: 400, overflow: 'auto' }}>
                  {users.map((user, index) => (
                    <Box key={user.id} sx={{ 
                      display: 'flex', 
                      justifyContent: 'space-between', 
                      alignItems: 'center',
                      py: 1,
                      borderBottom: index < users.length - 1 ? '1px solid #eee' : 'none'
                    }}>
                      <Box>
                        <Typography variant="body2" fontWeight="bold">
                          {getUserDisplayName(user)}
                        </Typography>
                        <Typography variant="caption" color="textSecondary">
                          {user.email}
                        </Typography>
                      </Box>
                      <Chip 
                        size="small" 
                        label={user.fcmToken ? "🟢" : "🔴"} 
                        title={user.fcmToken ? "Bildirim etkin" : "Bildirim kapalı"}
                      />
                    </Box>
                  ))}
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Örnekler */}
      <Grid container spacing={2} sx={{ mt: 2 }}>
        <Grid item xs={12}>
          <Card sx={{ bgcolor: '#f5f5f5' }}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>
                📝 Bildirim Örnekleri
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={4}>
                  <Button 
                    variant="text" 
                    fullWidth 
                    sx={{ justifyContent: 'flex-start', textAlign: 'left' }}
                    onClick={() => {
                      setTitle('Yeni Anket Mevcut!');
                      setMessage('Hemen katılın ve puan kazanın! 🎯');
                    }}
                  >
                    💼 Yeni Anket Bildirimi
                  </Button>
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Button 
                    variant="text" 
                    fullWidth 
                    sx={{ justifyContent: 'flex-start', textAlign: 'left' }}
                    onClick={() => {
                      setTitle('Ödemeniz Hazır!');
                      setMessage('Kazandığınız para hesabınıza yatırıldı! 💰');
                    }}
                  >
                    💰 Ödeme Bildirimi
                  </Button>
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Button 
                    variant="text" 
                    fullWidth 
                    sx={{ justifyContent: 'flex-start', textAlign: 'left' }}
                    onClick={() => {
                      setTitle('Güncellemeler Var!');
                      setMessage('Uygulamayı güncelleyerek yeni özellikleri keşfedin! 🚀');
                    }}
                  >
                    🚀 Güncelleme Bildirimi
                  </Button>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default NotificationManagement;
