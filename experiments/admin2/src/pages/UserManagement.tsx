import React, { useState, useEffect } from 'react';
import {
  Box, Button, Typography, TextField, Dialog, DialogActions,
  DialogContent, DialogTitle, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, IconButton,
  Chip, Card, CardContent, Grid, Alert, Snackbar, DialogContentText
} from '@mui/material';
import {
  Delete as DeleteIcon,
  Edit as EditIcon,
  Person as PersonIcon,
  Visibility as ViewIcon,
  Add as AddIcon,
  Remove as RemoveIcon,
  Star as StarIcon
} from '@mui/icons-material';

const API_BASE_URL = 'https://kazanion.onrender.com/api';

interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  points: number;
  balance: number;
  passwordHash: string;
  phoneNumber?: string;
  birthDate?: string;
}

const UserManagement = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [newUser, setNewUser] = useState({
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    points: 0,
    balance: 0.0,
    passwordHash: ''
  });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [isEditMode, setIsEditMode] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [confirmText, setConfirmText] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });
  
  // Puan yönetimi state'leri
  const [pointsDialogOpen, setPointsDialogOpen] = useState(false);
  const [pointsChange, setPointsChange] = useState('');
  const [pointsReason, setPointsReason] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/users`);
      const data = await response.json();
      setUsers(data);
    } catch (error) {
      console.error('Kullanıcılar çekilirken hata oluştu:', error);
      setError('Kullanıcılar yüklenirken hata oluştu');
      setSnackbar({ 
        open: true, 
        message: 'Kullanıcılar yüklenirken hata oluştu', 
        severity: 'error' 
      });
    } finally {
      setLoading(false);
    }
  };

  const handlePointsClick = (user: User) => {
    setSelectedUser(user);
    setPointsChange('');
    setPointsReason('');
    setPointsDialogOpen(true);
  };

  const handlePointsUpdate = async () => {
    if (!selectedUser || !pointsChange || !pointsReason) {
      setSnackbar({ 
        open: true, 
        message: 'Tüm alanları doldurun!', 
        severity: 'error' 
      });
      return;
    }

    const pointsChangeNum = parseInt(pointsChange);
    if (isNaN(pointsChangeNum)) {
      setSnackbar({ 
        open: true, 
        message: 'Geçerli bir puan değeri girin!', 
        severity: 'error' 
      });
      return;
    }

    const newPoints = selectedUser.points + pointsChangeNum;
    if (newPoints < 0) {
      setSnackbar({ 
        open: true, 
        message: 'Kullanıcı puanı 0\'dan az olamaz!', 
        severity: 'error' 
      });
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/users/${selectedUser.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: selectedUser.username,
          email: selectedUser.email,
          firstName: selectedUser.firstName,
          lastName: selectedUser.lastName,
          phoneNumber: selectedUser.phoneNumber,
          birthDate: selectedUser.birthDate,
          points: newPoints,
          balance: selectedUser.balance,
          passwordHash: selectedUser.passwordHash
        }),
      });

      if (response.ok) {
        setSnackbar({ 
          open: true, 
          message: `${selectedUser.username} kullanıcısının puanı güncellendi (${pointsChangeNum > 0 ? '+' : ''}${pointsChangeNum})`, 
          severity: 'success' 
        });
        loadUsers();
      } else {
        throw new Error('Puan güncelleme başarısız');
      }
    } catch (error) {
      console.error('Puan güncellenirken hata:', error);
      setSnackbar({ 
        open: true, 
        message: 'Puan güncellenirken hata oluştu', 
        severity: 'error' 
      });
    }

    setPointsDialogOpen(false);
    setSelectedUser(null);
    setPointsChange('');
    setPointsReason('');
  };

  const handleDeleteClick = (user: User) => {
    setSelectedUser(user);
    setDeleteDialogOpen(true);
    setConfirmText('');
  };

  const handleDeleteConfirm = async () => {
    if (!selectedUser || confirmText !== selectedUser.username) {
      setSnackbar({ 
        open: true, 
        message: 'Doğrulama metni hatalı!', 
        severity: 'error' 
      });
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/users/${selectedUser.id}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        setSnackbar({ 
          open: true, 
          message: `${selectedUser.username} kullanıcısı başarıyla silindi`, 
          severity: 'success' 
        });
        loadUsers();
      } else {
        throw new Error('Silme işlemi başarısız');
      }
    } catch (error) {
      console.error('Kullanıcı silinirken hata:', error);
      setSnackbar({ 
        open: true, 
        message: 'Kullanıcı silinirken hata oluştu', 
        severity: 'error' 
      });
    }

    setDeleteDialogOpen(false);
    setSelectedUser(null);
    setConfirmText('');
  };

  const handleDeleteTestUsers = async () => {
    const testUsers = users.filter(user => 
      user.username.includes('test') || 
      user.username.includes('debug') || 
      user.username === 'yeni_kullanici'
    );

    if (testUsers.length === 0) {
      setSnackbar({ 
        open: true, 
        message: 'Silinecek test kullanıcısı bulunamadı', 
        severity: 'success' 
      });
      return;
    }

    try {
      for (const user of testUsers) {
        await fetch(`${API_BASE_URL}/users/${user.id}`, {
          method: 'DELETE',
        });
      }
      
      setSnackbar({ 
        open: true, 
        message: `${testUsers.length} test kullanıcısı silindi`, 
        severity: 'success' 
      });
      loadUsers();
    } catch (error) {
      console.error('Test kullanıcıları silinirken hata:', error);
      setSnackbar({ 
        open: true, 
        message: 'Test kullanıcıları silinirken hata oluştu', 
        severity: 'error' 
      });
    }
  };

  const getDisplayName = (user: User) => {
    if (user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`;
    } else if (user.firstName) {
      return user.firstName;
    } else {
      return user.username;
    }
  };

  const isTestUser = (username: string) => {
    return username.includes('test') || 
           username.includes('debug') || 
           username === 'yeni_kullanici';
  };

  const resetAllPoints = async () => {
    if (window.confirm('Tüm kullanıcıların puanlarını sıfırlamak istediğinizden emin misiniz?')) {
      try {
        for (const user of users) {
          await fetch(`${API_BASE_URL}/users/${user.id}`, {
            method: 'PUT',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              username: user.username,
              email: user.email,
              firstName: user.firstName,
              lastName: user.lastName,
              phoneNumber: user.phoneNumber,
              birthDate: user.birthDate,
              points: 0,
              balance: user.balance,
              passwordHash: user.passwordHash
            }),
          });
        }
        
        setSnackbar({ 
          open: true, 
          message: 'Tüm kullanıcıların puanları sıfırlandı', 
          severity: 'success' 
        });
        loadUsers();
      } catch (error) {
        console.error('Puanlar sıfırlanırken hata:', error);
        setSnackbar({ 
          open: true, 
          message: 'Puanlar sıfırlanırken hata oluştu', 
          severity: 'error' 
        });
      }
    }
  };

  if (loading) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography>Yükleniyor...</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ padding: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Kullanıcı Yönetimi
        </Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={loadUsers}
          startIcon={<PersonIcon />}
        >
          Yenile
        </Button>
      </Box>

      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}

      {/* İstatistikler */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Toplam Kullanıcı
              </Typography>
              <Typography variant="h5">
                {users.length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Gerçek Kullanıcılar
              </Typography>
              <Typography variant="h5">
                {users.filter(u => !isTestUser(u.username)).length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Test Kullanıcıları
              </Typography>
              <Typography variant="h5">
                {users.filter(u => isTestUser(u.username)).length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Toplam Puan
              </Typography>
              <Typography variant="h5">
                {users.reduce((sum, u) => sum + u.points, 0)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Aksiyonlar */}
      <Box sx={{ mb: 2 }}>
        <Button 
          variant="outlined" 
          color="warning"
          onClick={handleDeleteTestUsers}
          sx={{ mr: 2 }}
        >
          Test Kullanıcılarını Temizle
        </Button>
        <Button 
          variant="outlined" 
          color="error"
          onClick={resetAllPoints}
          sx={{ mr: 2 }}
        >
          Tüm Puanları Sıfırla
        </Button>
        <Button 
          variant="outlined" 
          onClick={loadUsers}
        >
          Yenile
        </Button>
      </Box>

      {/* Kullanıcılar Tablosu */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Kullanıcı Adı</TableCell>
              <TableCell>Ad Soyad</TableCell>
              <TableCell>E-posta</TableCell>
              <TableCell>Telefon</TableCell>
              <TableCell>Puan</TableCell>
              <TableCell>Bakiye</TableCell>
              <TableCell>Durum</TableCell>
              <TableCell>İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <PersonIcon fontSize="small" />
                    {user.username}
                  </Box>
                </TableCell>
                <TableCell>{getDisplayName(user)}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>{user.phoneNumber || '-'}</TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <StarIcon fontSize="small" color="warning" />
                    {user.points}
                  </Box>
                </TableCell>
                <TableCell>₺{user.balance.toFixed(2)}</TableCell>
                <TableCell>
                  {isTestUser(user.username) ? (
                    <Chip label="Test" color="warning" size="small" />
                  ) : (
                    <Chip label="Gerçek" color="success" size="small" />
                  )}
                </TableCell>
                <TableCell>
                  <IconButton 
                    size="small" 
                    color="warning"
                    onClick={() => handlePointsClick(user)}
                    title="Puan Düzenle"
                  >
                    <StarIcon />
                  </IconButton>
                  <IconButton size="small" color="primary" title="Görüntüle">
                    <ViewIcon />
                  </IconButton>
                  <IconButton 
                    size="small" 
                    color="error"
                    onClick={() => handleDeleteClick(user)}
                    title="Sil"
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Puan Düzenleme Dialog */}
      <Dialog open={pointsDialogOpen} onClose={() => setPointsDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          Puan Düzenle - {selectedUser?.username}
        </DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            <strong>{selectedUser?.username}</strong> kullanıcısının mevcut puanı: <strong>{selectedUser?.points}</strong>
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            label="Puan Değişikliği (+/- değer girin)"
            type="number"
            fullWidth
            variant="outlined"
            value={pointsChange}
            onChange={(e) => setPointsChange(e.target.value)}
            helperText="Örnek: +200 (basit anket), +300 (linkli anket), +500 (premium anket)"
            sx={{ mb: 2 }}
          />
          <TextField
            margin="dense"
            label="Değişiklik Sebebi"
            fullWidth
            variant="outlined"
            value={pointsReason}
            onChange={(e) => setPointsReason(e.target.value)}
            helperText="Puan değişikliğinin sebebini yazın"
          />
          {pointsChange && !isNaN(parseInt(pointsChange)) && (
            <DialogContentText sx={{ mt: 2, fontWeight: 'bold' }}>
              Yeni puan: {(selectedUser?.points || 0) + parseInt(pointsChange)}
            </DialogContentText>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPointsDialogOpen(false)}>İptal</Button>
          <Button onClick={handlePointsUpdate} variant="contained">
            Güncelle
          </Button>
        </DialogActions>
      </Dialog>

      {/* Silme Onay Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          Kullanıcı Sil
        </DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            <strong>{selectedUser?.username}</strong> kullanıcısını silmek istediğinizden emin misiniz?
            Bu işlem geri alınamaz!
          </DialogContentText>
          <DialogContentText sx={{ mb: 2 }}>
            Kullanıcı Bilgileri:
            <br />• Ad: {getDisplayName(selectedUser!)}
            <br />• E-posta: {selectedUser?.email}
            <br />• Puan: {selectedUser?.points}
            <br />• Bakiye: ₺{selectedUser?.balance.toFixed(2)}
          </DialogContentText>
          <DialogContentText sx={{ mb: 2, fontWeight: 'bold' }}>
            Onaylamak için kullanıcı adını yazın: <code>{selectedUser?.username}</code>
          </DialogContentText>
          <TextField
            fullWidth
            label="Kullanıcı adını buraya yazın"
            value={confirmText}
            onChange={(e) => setConfirmText(e.target.value)}
            error={confirmText !== '' && confirmText !== selectedUser?.username}
            helperText={confirmText !== '' && confirmText !== selectedUser?.username ? 'Kullanıcı adı eşleşmiyor' : ''}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>İptal</Button>
          <Button 
            onClick={handleDeleteConfirm} 
            color="error" 
            variant="contained"
            disabled={confirmText !== selectedUser?.username}
          >
            Sil
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default UserManagement; 