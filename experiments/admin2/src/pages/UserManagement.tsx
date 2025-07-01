import React, { useState, useEffect } from 'react';
import {
  Box, Button, Typography, TextField, Dialog, DialogActions,
  DialogContent, DialogTitle, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, IconButton,
  Chip, Card, CardContent, Grid
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import PersonIcon from '@mui/icons-material/Person';
import EmailIcon from '@mui/icons-material/Email';
import StarsIcon from '@mui/icons-material/Stars';

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
  const [loading, setLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/users`);
      const data = await response.json();
      setUsers(data);
    } catch (error) {
      console.error('Kullanıcılar çekilirken hata oluştu:', error);
      setError('Kullanıcılar yüklenirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDialog = (user?: User) => {
    if (user) {
      setSelectedUser(user);
      setNewUser({
        username: user.username,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        points: user.points,
        balance: user.balance,
        passwordHash: user.passwordHash
      });
      setIsEditMode(true);
    } else {
      setSelectedUser(null);
      setNewUser({
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        points: 0,
        balance: 0.0,
        passwordHash: ''
      });
      setIsEditMode(false);
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedUser(null);
    setIsEditMode(false);
  };

  const handleSaveUser = async () => {
    try {
      setLoading(true);
      let response;
      
      if (isEditMode && selectedUser) {
        // Update existing user
        response = await fetch(`${API_BASE_URL}/users/${selectedUser.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(newUser),
        });
      } else {
        // Create new user
        response = await fetch(`${API_BASE_URL}/users`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(newUser),
        });
      }

      if (response.ok) {
        fetchUsers();
        handleCloseDialog();
      } else {
        setError('Kullanıcı kaydedilirken hata oluştu');
      }
    } catch (error) {
      console.error('Kullanıcı kaydedilirken hata:', error);
      setError('Kullanıcı kaydedilirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId: number) => {
    if (window.confirm('Bu kullanıcıyı silmek istediğinizden emin misiniz?')) {
      try {
        const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
          method: 'DELETE',
        });

        if (response.ok) {
          fetchUsers();
        } else {
          setError('Kullanıcı silinirken hata oluştu');
        }
      } catch (error) {
        console.error('Kullanıcı silinirken hata:', error);
        setError('Kullanıcı silinirken hata oluştu');
      }
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

  const getActivityStatus = (user: User) => {
    // Basit aktiflik kontrolü - puanı 0'dan fazla olanlar aktif sayılsın
    return user.points > 0 ? 'Aktif' : 'Pasif';
  };

  return (
    <Box sx={{ padding: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Kullanıcı Yönetimi
        </Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={() => handleOpenDialog()}
          startIcon={<PersonIcon />}
        >
          Yeni Kullanıcı Ekle
        </Button>
      </Box>

      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}

      {loading ? (
        <Typography>Yükleniyor...</Typography>
      ) : (
        <>
          {/* Özet Kartları */}
          <Grid container spacing={3} sx={{ mb: 3 }}>
            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <PersonIcon color="primary" sx={{ mr: 1 }} />
                    <div>
                      <Typography color="textSecondary" gutterBottom>
                        Toplam Kullanıcı
                      </Typography>
                      <Typography variant="h4">
                        {users.length}
                      </Typography>
                    </div>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <StarsIcon color="warning" sx={{ mr: 1 }} />
                    <div>
                      <Typography color="textSecondary" gutterBottom>
                        Aktif Kullanıcılar
                      </Typography>
                      <Typography variant="h4">
                        {users.filter(user => user.points > 0).length}
                      </Typography>
                    </div>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <StarsIcon color="success" sx={{ mr: 1 }} />
                    <div>
                      <Typography color="textSecondary" gutterBottom>
                        Toplam Puan
                      </Typography>
                      <Typography variant="h4">
                        {users.reduce((sum, user) => sum + user.points, 0)}
                      </Typography>
                    </div>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <EmailIcon color="info" sx={{ mr: 1 }} />
                    <div>
                      <Typography color="textSecondary" gutterBottom>
                        Toplam Bakiye
                      </Typography>
                      <Typography variant="h4">
                        ₺{users.reduce((sum, user) => sum + user.balance, 0).toFixed(2)}
                      </Typography>
                    </div>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          {/* Kullanıcılar Tablosu */}
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Ad Soyad</TableCell>
                  <TableCell>Kullanıcı Adı</TableCell>
                  <TableCell>E-posta</TableCell>
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
                    <TableCell>{getDisplayName(user)}</TableCell>
                    <TableCell>{user.username}</TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>
                      <Chip 
                        label={user.points} 
                        color="primary" 
                        size="small"
                        icon={<StarsIcon />}
                      />
                    </TableCell>
                    <TableCell>₺{user.balance.toFixed(2)}</TableCell>
                    <TableCell>
                      <Chip 
                        label={getActivityStatus(user)} 
                        color={user.points > 0 ? "success" : "default"} 
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <IconButton 
                        onClick={() => handleOpenDialog(user)}
                        color="primary"
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton 
                        onClick={() => handleDeleteUser(user.id)}
                        color="error"
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}

      {/* Kullanıcı Ekleme/Düzenleme Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {isEditMode ? 'Kullanıcı Düzenle' : 'Yeni Kullanıcı Ekle'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Ad"
                value={newUser.firstName}
                onChange={(e) => setNewUser({ ...newUser, firstName: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Soyad"
                value={newUser.lastName}
                onChange={(e) => setNewUser({ ...newUser, lastName: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Kullanıcı Adı"
                value={newUser.username}
                onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="E-posta"
                type="email"
                value={newUser.email}
                onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Puan"
                type="number"
                value={newUser.points}
                onChange={(e) => setNewUser({ ...newUser, points: parseInt(e.target.value) || 0 })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Bakiye (₺)"
                type="number"
                inputProps={{ step: "0.01" }}
                value={newUser.balance}
                onChange={(e) => setNewUser({ ...newUser, balance: parseFloat(e.target.value) || 0 })}
              />
            </Grid>
            {!isEditMode && (
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Şifre"
                  type="password"
                  value={newUser.passwordHash}
                  onChange={(e) => setNewUser({ ...newUser, passwordHash: e.target.value })}
                />
              </Grid>
            )}
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>İptal</Button>
          <Button onClick={handleSaveUser} variant="contained">
            {isEditMode ? 'Güncelle' : 'Ekle'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default UserManagement; 