import React, { useState, useEffect, Component, ErrorInfo, ReactNode } from 'react';
import {
  Box, Button, Typography, Dialog, DialogActions,
  DialogContent, DialogTitle, Chip, Card, CardContent, 
  Grid, Alert, Snackbar, Tabs, Tab, List, ListItem, 
  ListItemText, ListItemIcon, Paper, Table, 
  TableBody, TableCell, TableContainer, TableHead, 
  TableRow, TextField, InputAdornment, TableSortLabel,
  Pagination, FormControl, InputLabel, Select, MenuItem,
  Avatar, Tooltip, IconButton
} from '@mui/material';
import {
  Visibility as ViewIcon,
  Person as PersonIcon,
  Security as SecurityIcon,
  Block as BlockIcon,
  CheckCircle as CheckCircleIcon,
  Warning as WarningIcon,
  LocationOn as LocationIcon,
  Star as StarIcon,
  CalendarToday as CalendarIcon,
  Search as SearchIcon,
  Add as AddIcon,
  Email as EmailIcon,
  Badge as BadgeIcon,
  VpnKey as AccessIcon,
  FilterList as FilterIcon,
  Delete as DeleteIcon,
  Settings as SettingsIcon
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
  registrationDate?: string;
  lastLoginDate?: string;
  accountStatus: 'Aktif' | 'İnceleme Altında' | 'Banlı' | 'Beklemede';
  userType: 'Standard' | 'Premium' | 'Admin' | 'Moderator';
  securityLevel: 'Düşük' | 'Orta' | 'Yüksek';
  ipAddress?: string;
  securityNotes?: string[];
  completedSurveys?: Survey[];
  company?: string;
  jobTitle?: string;
  address1?: string;
  address2?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
  gender?: 'Erkek' | 'Kadın' | 'Diğer';
  age?: number;
  language: 'tr' | 'en' | 'de' | 'fr';
  timeZone?: string;
  lastActivityDate?: string;
  totalEarnings?: number;
  completedSurveysCount?: number;
  averageRating?: number;
  isVerified: boolean;
  isActive: boolean;
  profileCompleteness?: number;
}

interface Survey {
  id: number;
  title: string;
  completedAt: string;
  reward: number;
  location?: string;
  rating?: number;
  duration?: number;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error?: Error;
}

interface ErrorBoundaryProps {
  children: ReactNode;
}

// Ipsos iField tarzında mock data
const mockUsers: User[] = [
  {
    id: 1001,
    username: "ahmet.yilmaz",
    email: "ahmet@ipsos.com",
    firstName: "Ahmet",
    lastName: "Yılmaz",
    points: 2450,
    balance: 245.50,
    passwordHash: "hashedpass123",
    phoneNumber: "+90 532 123 4567",
    birthDate: "1985-03-15",
    registrationDate: "2024-01-15",
    lastLoginDate: "2024-01-25",
    lastActivityDate: "2024-01-25",
    accountStatus: "Aktif",
    userType: "Standard",
    securityLevel: "Yüksek",
    ipAddress: "192.168.1.45",
    securityNotes: ["Telefon doğrulaması tamamlandı", "IP kontrolü yapıldı"],
    company: "Ipsos Türkiye",
    jobTitle: "Araştırma Uzmanı",
    address1: "Centrum İş Merkezi Aydınevler",
    address2: "Küçükyalı, İstanbul",
    city: "İstanbul",
    state: "Marmara",
    postalCode: "34854",
    country: "Türkiye",
    gender: "Erkek",
    age: 39,
    language: "tr",
    timeZone: "Europe/Istanbul",
    totalEarnings: 1250.75,
    completedSurveysCount: 45,
    averageRating: 4.8,
    isVerified: true,
    isActive: true,
    profileCompleteness: 95,
    completedSurveys: [
      { id: 101, title: "Teknoloji Anketi", completedAt: "2024-01-20", reward: 50, location: "İstanbul", rating: 5, duration: 15 },
      { id: 102, title: "Yemek Tercihleri", completedAt: "2024-01-22", reward: 30, location: "İstanbul", rating: 4, duration: 10 }
    ]
  },
  {
    id: 1002,
    username: "zeynep.kaya",
    email: "zeynep@ipsos.com",
    firstName: "Zeynep",
    lastName: "Kaya",
    points: 1850,
    balance: 185.00,
    passwordHash: "hashedpass456",
    phoneNumber: "+90 505 987 6543",
    birthDate: "1992-07-22",
    registrationDate: "2024-01-10",
    lastLoginDate: "2024-01-24",
    lastActivityDate: "2024-01-24",
    accountStatus: "İnceleme Altında",
    userType: "Premium",
    securityLevel: "Orta",
    ipAddress: "10.0.0.123",
    securityNotes: ["Çoklu hesap şüphesi", "Manuel inceleme gerekli"],
    company: "Freelance",
    jobTitle: "Pazarlama Uzmanı",
    address1: "Bornova Mahallesi",
    address2: "İzmir Merkez",
    city: "İzmir",
    state: "Ege",
    postalCode: "35100",
    country: "Türkiye",
    gender: "Kadın",
    age: 32,
    language: "tr",
    timeZone: "Europe/Istanbul",
    totalEarnings: 750.25,
    completedSurveysCount: 22,
    averageRating: 4.2,
    isVerified: false,
    isActive: true,
    profileCompleteness: 78,
    completedSurveys: [
      { id: 103, title: "Alışveriş Alışkanlıkları", completedAt: "2024-01-18", reward: 40, location: "İzmir", rating: 4, duration: 12 }
    ]
  },
  {
    id: 1003,
    username: "mehmet.banned",
    email: "mehmet@banned.com",
    firstName: "Mehmet",
    lastName: "Demir",
    points: 0,
    balance: 0.00,
    passwordHash: "hashedpass789",
    phoneNumber: "+90 555 111 2233",
    birthDate: "1988-12-05",
    registrationDate: "2024-01-05",
    lastLoginDate: "2024-01-10",
    lastActivityDate: "2024-01-10",
    accountStatus: "Banlı",
    userType: "Standard",
    securityLevel: "Düşük",
    ipAddress: "172.16.0.99",
    securityNotes: ["Sahte anket cevapları", "Kalıcı ban uygulandı"],
    company: "Bilinmiyor",
    jobTitle: "Çalışmıyor",
    address1: "Bilinmeyen Adres",
    city: "Ankara",
    state: "İç Anadolu",
    postalCode: "06000",
    country: "Türkiye",
    gender: "Erkek",
    age: 36,
    language: "tr",
    timeZone: "Europe/Istanbul",
    totalEarnings: 0,
    completedSurveysCount: 0,
    averageRating: 0,
    isVerified: false,
    isActive: false,
    profileCompleteness: 45,
    completedSurveys: []
  }
];

class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    console.error('🚨 ErrorBoundary yakaladı:', error);
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('🚨 ComponentDidCatch:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <Box sx={{ p: 3 }}>
          <Alert severity="error" sx={{ mb: 2 }}>
            <Typography variant="h6">❌ Component Hatası</Typography>
            <Typography variant="body2">
              {this.state.error?.message || 'Bilinmeyen hata'}
            </Typography>
          </Alert>
          <Button 
            variant="contained" 
            onClick={() => this.setState({ hasError: false, error: undefined })}
          >
            Tekrar Dene
          </Button>
        </Box>
      );
    }

    return this.props.children;
  }
}

const UserManagement = () => {
  console.log('🚀 Professional UserManagement component yüklendi');
  
  const [users, setUsers] = useState<User[]>([]);
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [detailDialogOpen, setDetailDialogOpen] = useState(false);
  const [tabValue, setTabValue] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(1);
  const [orderBy, setOrderBy] = useState<keyof User>('id');
  const [order, setOrder] = useState<'asc' | 'desc'>('asc');
  
  // Advanced filters
  const [filterAccountStatus, setFilterAccountStatus] = useState<string>('');
  const [filterCountry, setFilterCountry] = useState<string>('');
  const [filterVerified, setFilterVerified] = useState<string>('');
  const [showFilters, setShowFilters] = useState(false);
  
  const [snackbar, setSnackbar] = useState({ 
    open: false, 
    message: '', 
    severity: 'success' as 'success' | 'error' | 'warning' | 'info' 
  });

  const itemsPerPage = 15;

  useEffect(() => {
    console.log('🔄 useEffect çalıştı, loadUsers çağrılıyor');
    loadUsers();
  }, []);

  useEffect(() => {
    // Advanced search and filtering
    const filtered = users.filter(user => {
      const searchMatch = 
        `${user.firstName} ${user.lastName}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.city?.toLowerCase().includes(searchTerm.toLowerCase());
      
      const statusMatch = !filterAccountStatus || user.accountStatus === filterAccountStatus;
      const countryMatch = !filterCountry || user.country === filterCountry;
      const verifiedMatch = !filterVerified || 
        (filterVerified === 'verified' && user.isVerified) ||
        (filterVerified === 'unverified' && !user.isVerified);
      
      return searchMatch && statusMatch && countryMatch && verifiedMatch;
    });
    
    setFilteredUsers(filtered);
    setPage(1);
  }, [users, searchTerm, filterAccountStatus, filterCountry, filterVerified]);

  const loadUsers = async () => {
    console.log('🔍 Loading users başladı...');
    setLoading(true);
    setError(null);
    
    try {
      console.log('🌐 API çağrısı yapılıyor:', `${API_BASE_URL}/users`);
      const response = await fetch(`${API_BASE_URL}/users`);
      console.log('📡 Response alındı:', response.status, response.ok);
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      const backendUsers = await response.json();
      console.log('📊 Backend kullanıcı verisi alındı:', backendUsers.length, 'kullanıcı');
      
      // Backend kullanıcılarını professional format'a çevir
      const formattedBackendUsers: User[] = backendUsers.map((user: any) => ({
        ...user,
        registrationDate: user.registrationDate || "2024-01-01",
        lastLoginDate: user.lastLoginDate || "2024-01-25",
        lastActivityDate: user.lastActivityDate || "2024-01-25",
        accountStatus: "Aktif" as const,
        userType: "Standard" as const,
        securityLevel: "Orta" as const,
        ipAddress: "192.168.1.100",
        securityNotes: ["Backend kullanıcısı"],
        company: "KazaniOn",
        jobTitle: "Kullanıcı",
        city: "İstanbul",
        country: "Türkiye",
        gender: "Erkek" as const,
        language: "tr" as const,
        isVerified: true,
        isActive: true,
        profileCompleteness: 60,
        completedSurveys: [],
        completedSurveysCount: 0,
        totalEarnings: user.balance || 0,
        averageRating: 4.0
      }));
      
      // Backend ve mock kullanıcıları birleştir
      const allUsers = [...formattedBackendUsers, ...mockUsers];
      setUsers(allUsers);
      
      setSnackbar({ 
        open: true, 
        message: `✅ ${allUsers.length} kullanıcı yüklendi (${backendUsers.length} gerçek, ${mockUsers.length} demo)`, 
        severity: 'success' 
      });
    } catch (error) {
      console.error('❌ Kullanıcılar çekilirken hata oluştu:', error);
      setUsers(mockUsers);
      const errorMessage = `API Hatası: ${error instanceof Error ? error.message : 'Bilinmeyen hata'}. Demo veriler yüklendi.`;
      setError(errorMessage);
      setSnackbar({ 
        open: true, 
        message: errorMessage, 
        severity: 'warning' 
      });
    } finally {
      setLoading(false);
      console.log('✅ Loading users tamamlandı');
    }
  };

  const handleViewDetails = (user: User) => {
    setSelectedUser(user);
    setDetailDialogOpen(true);
    setTabValue(0);
  };

  const handleAccountStatusChange = (newStatus: User['accountStatus']) => {
    if (!selectedUser) return;
    
    const updatedUsers = users.map(user => 
      user.id === selectedUser.id 
        ? { ...user, accountStatus: newStatus }
        : user
    );
    
    setUsers(updatedUsers);
    setSelectedUser({ ...selectedUser, accountStatus: newStatus });
    
    setSnackbar({
      open: true,
      message: `${selectedUser.firstName} ${selectedUser.lastName} hesap durumu "${newStatus}" olarak güncellendi`,
      severity: 'success'
    });
  };

  const getStatusChip = (status: User['accountStatus']) => {
    switch (status) {
      case 'Aktif':
        return <Chip label="Aktif" color="success" size="small" icon={<CheckCircleIcon />} />;
      case 'İnceleme Altında':
        return <Chip label="İnceleme Altında" color="warning" size="small" icon={<WarningIcon />} />;
      case 'Banlı':
        return <Chip label="Banlı" color="error" size="small" icon={<BlockIcon />} />;
      case 'Beklemede':
        return <Chip label="Beklemede" color="info" size="small" icon={<AccessIcon />} />;
      default:
        return <Chip label="Bilinmiyor" color="default" size="small" />;
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('tr-TR');
  };

  const handleSort = (property: keyof User) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
  };

  const sortedUsers = [...filteredUsers].sort((a, b) => {
    if (orderBy === 'balance' || orderBy === 'points' || orderBy === 'id' || orderBy === 'age' || orderBy === 'completedSurveysCount') {
      const aVal = Number(a[orderBy]) || 0;
      const bVal = Number(b[orderBy]) || 0;
      return order === 'asc' ? aVal - bVal : bVal - aVal;
    } else {
      const aVal = String(a[orderBy] || '').toLowerCase();
      const bVal = String(b[orderBy] || '').toLowerCase();
      return order === 'asc' 
        ? aVal.localeCompare(bVal)
        : bVal.localeCompare(aVal);
    }
  });

  const paginatedUsers = sortedUsers.slice(
    (page - 1) * itemsPerPage,
    page * itemsPerPage
  );

  const totalPages = Math.ceil(sortedUsers.length / itemsPerPage);

  const clearFilters = () => {
    setFilterAccountStatus('');
    setFilterCountry('');
    setFilterVerified('');
    setSearchTerm('');
  };

  if (loading) {
    console.log('⏳ Loading state aktif');
    return (
      <Box sx={{ p: 3 }}>
        <Typography>Yükleniyor...</Typography>
      </Box>
    );
  }

  console.log('📋 Professional UserManagement render ediliyor. Users array:', users.length, 'kullanıcı');

  return (
    <Box sx={{ padding: 3, backgroundColor: '#f8f9fa', minHeight: '100vh' }}>
      {/* Professional Header */}
      <Paper sx={{ p: 3, mb: 3, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box>
            <Typography variant="h4" component="h1" sx={{ display: 'flex', alignItems: 'center', gap: 1, fontWeight: 600 }}>
              <PersonIcon fontSize="large" />
              Kullanıcı Yönetimi Sistemi
            </Typography>
            <Typography variant="body1" sx={{ mt: 1, opacity: 0.9 }}>
              Professional iField benzeri kullanıcı yönetimi ve analiz platformu
            </Typography>
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button
              variant="contained"
              color="inherit"
              onClick={loadUsers}
              startIcon={<PersonIcon />}
              sx={{ backgroundColor: 'rgba(255,255,255,0.2)', backdropFilter: 'blur(10px)' }}
            >
              Yenile
            </Button>
            <Button
              variant="contained"
              color="inherit"
              startIcon={<AddIcon />}
              sx={{ backgroundColor: 'rgba(255,255,255,0.2)', backdropFilter: 'blur(10px)' }}
            >
              Yeni Kullanıcı
            </Button>
          </Box>
        </Box>
      </Paper>

      {error && (
        <Alert severity="warning" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {/* Professional Statistics Cards */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} md={3}>
          <Card sx={{ height: '100%', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="inherit" gutterBottom variant="body2" sx={{ opacity: 0.8 }}>
                    Toplam Kullanıcı
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 600 }}>
                    {users.length}
                  </Typography>
                </Box>
                <PersonIcon sx={{ fontSize: 40, opacity: 0.7 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card sx={{ height: '100%', background: 'linear-gradient(135deg, #4CAF50 0%, #45a049 100%)', color: 'white' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="inherit" gutterBottom variant="body2" sx={{ opacity: 0.8 }}>
                    Aktif Kullanıcılar
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 600 }}>
                    {users.filter(u => u.accountStatus === 'Aktif').length}
                  </Typography>
                </Box>
                <CheckCircleIcon sx={{ fontSize: 40, opacity: 0.7 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card sx={{ height: '100%', background: 'linear-gradient(135deg, #FF9800 0%, #F57C00 100%)', color: 'white' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="inherit" gutterBottom variant="body2" sx={{ opacity: 0.8 }}>
                    Doğrulanmış
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 600 }}>
                    {users.filter(u => u.isVerified).length}
                  </Typography>
                </Box>
                <BadgeIcon sx={{ fontSize: 40, opacity: 0.7 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card sx={{ height: '100%', background: 'linear-gradient(135deg, #2196F3 0%, #1976D2 100%)', color: 'white' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="inherit" gutterBottom variant="body2" sx={{ opacity: 0.8 }}>
                    Toplam Kazanç
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 600 }}>
                    ₺{users.reduce((sum, u) => sum + (u.totalEarnings || 0), 0).toLocaleString()}
                  </Typography>
                </Box>
                <StarIcon sx={{ fontSize: 40, opacity: 0.7 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Advanced Search and Filters */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <SearchIcon />
            Gelişmiş Arama ve Filtreler
          </Typography>
          <Box>
            <Button
              variant="outlined"
              onClick={() => setShowFilters(!showFilters)}
              startIcon={<FilterIcon />}
              sx={{ mr: 2 }}
            >
              {showFilters ? 'Filtreleri Gizle' : 'Filtreleri Göster'}
            </Button>
            <Button
              variant="outlined"
              onClick={clearFilters}
              color="warning"
            >
              Temizle
            </Button>
          </Box>
        </Box>
        
        {/* Main Search */}
        <TextField
          placeholder="Ad, soyad, e-posta, kullanıcı adı, şehir ile arama yapın..."
          variant="outlined"
          fullWidth
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          sx={{ mb: showFilters ? 2 : 0 }}
        />

        {/* Advanced Filters */}
        {showFilters && (
          <Grid container spacing={2}>
            <Grid item xs={12} md={4}>
              <FormControl fullWidth>
                <InputLabel>Hesap Durumu</InputLabel>
                <Select
                  value={filterAccountStatus}
                  onChange={(e) => setFilterAccountStatus(e.target.value)}
                  label="Hesap Durumu"
                >
                  <MenuItem value="">Tümü</MenuItem>
                  <MenuItem value="Aktif">Aktif</MenuItem>
                  <MenuItem value="İnceleme Altında">İnceleme Altında</MenuItem>
                  <MenuItem value="Banlı">Banlı</MenuItem>
                  <MenuItem value="Beklemede">Beklemede</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={4}>
              <FormControl fullWidth>
                <InputLabel>Ülke</InputLabel>
                <Select
                  value={filterCountry}
                  onChange={(e) => setFilterCountry(e.target.value)}
                  label="Ülke"
                >
                  <MenuItem value="">Tümü</MenuItem>
                  <MenuItem value="Türkiye">Türkiye</MenuItem>
                  <MenuItem value="Amerika">Amerika</MenuItem>
                  <MenuItem value="Almanya">Almanya</MenuItem>
                  <MenuItem value="İngiltere">İngiltere</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={4}>
              <FormControl fullWidth>
                <InputLabel>Doğrulama</InputLabel>
                <Select
                  value={filterVerified}
                  onChange={(e) => setFilterVerified(e.target.value)}
                  label="Doğrulama"
                >
                  <MenuItem value="">Tümü</MenuItem>
                  <MenuItem value="verified">Doğrulanmış</MenuItem>
                  <MenuItem value="unverified">Doğrulanmamış</MenuItem>
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        )}
        
        {/* Filter Summary */}
        <Box sx={{ mt: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
          <Typography variant="body2" color="text.secondary">
            {filteredUsers.length} kullanıcı gösteriliyor ({users.length} toplam)
          </Typography>
          {(searchTerm || filterAccountStatus || filterCountry || filterVerified) && (
            <Chip 
              label="Filtre aktif" 
              color="primary" 
              size="small" 
              onDelete={clearFilters}
              deleteIcon={<DeleteIcon />}
            />
          )}
        </Box>
      </Paper>

      {/* Professional User Table */}
      <Paper sx={{ width: '100%', overflow: 'hidden', boxShadow: 3 }}>
        <TableContainer sx={{ maxHeight: 650, overflowX: 'hidden', overflowY: 'auto' }}>
          <Table stickyHeader size="small" sx={{ width: '100%' }}>
            <TableHead>
              <TableRow sx={{ '& .MuiTableCell-head': { backgroundColor: '#f5f5f5', fontWeight: 600 } }}>
                <TableCell sx={{ width: 50 }}>
                  <TableSortLabel
                    active={orderBy === 'id'}
                    direction={orderBy === 'id' ? order : 'asc'}
                    onClick={() => handleSort('id')}
                  >
                    ID
                  </TableSortLabel>
                </TableCell>
                <TableCell sx={{ width: 180 }}>
                  <TableSortLabel
                    active={orderBy === 'firstName'}
                    direction={orderBy === 'firstName' ? order : 'asc'}
                    onClick={() => handleSort('firstName')}
                  >
                    AD SOYAD
                  </TableSortLabel>
                </TableCell>
                <TableCell sx={{ width: 130 }}>
                  <TableSortLabel
                    active={orderBy === 'username'}
                    direction={orderBy === 'username' ? order : 'asc'}
                    onClick={() => handleSort('username')}
                  >
                    KULLANICI ADI
                  </TableSortLabel>
                </TableCell>
                <TableCell sx={{ width: 200 }}>
                  <TableSortLabel
                    active={orderBy === 'email'}
                    direction={orderBy === 'email' ? order : 'asc'}
                    onClick={() => handleSort('email')}
                  >
                    E-POSTA
                  </TableSortLabel>
                </TableCell>
                <TableCell sx={{ width: 90 }}>ŞEHİR</TableCell>
                <TableCell sx={{ width: 90 }}>
                  <TableSortLabel
                    active={orderBy === 'totalEarnings'}
                    direction={orderBy === 'totalEarnings' ? order : 'asc'}
                    onClick={() => handleSort('totalEarnings')}
                  >
                    KAZANÇ
                  </TableSortLabel>
                </TableCell>
                <TableCell sx={{ width: 70 }}>
                  <TableSortLabel
                    active={orderBy === 'completedSurveysCount'}
                    direction={orderBy === 'completedSurveysCount' ? order : 'asc'}
                    onClick={() => handleSort('completedSurveysCount')}
                  >
                    ANKET
                  </TableSortLabel>
                </TableCell>
                <TableCell sx={{ width: 100 }}>
                  <TableSortLabel
                    active={orderBy === 'lastActivityDate'}
                    direction={orderBy === 'lastActivityDate' ? order : 'asc'}
                    onClick={() => handleSort('lastActivityDate')}
                  >
                    SON AKTİVİTE
                  </TableSortLabel>
                </TableCell>
                <TableCell sx={{ width: 100 }}>DURUM</TableCell>
                <TableCell sx={{ width: 90 }}>İŞLEMLER</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {paginatedUsers.map((user) => (
                <TableRow 
                  key={user.id} 
                  hover
                  sx={{ 
                    '&:hover': { backgroundColor: 'rgba(0, 0, 0, 0.04)' },
                    cursor: 'pointer',
                    borderLeft: user.isVerified ? '3px solid #4CAF50' : '3px solid transparent'
                  }}
                >
                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>
                      {user.id}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Avatar 
                        sx={{ 
                          width: 32, 
                          height: 32, 
                          backgroundColor: user.gender === 'Kadın' ? '#E91E63' : '#2196F3',
                          fontSize: '0.875rem'
                        }}
                      >
                        {user.firstName?.charAt(0)}{user.lastName?.charAt(0)}
                      </Avatar>
                      <Box sx={{ flex: 1, minWidth: 0 }}>
                        <Typography 
                          variant="body2" 
                          sx={{ 
                            fontWeight: 600,
                            overflow: 'hidden', 
                            textOverflow: 'ellipsis', 
                            whiteSpace: 'nowrap',
                            maxWidth: 120
                          }}
                          title={`${user.firstName || ''} ${user.lastName || ''}`.trim() || user.username}
                        >
                          {`${user.firstName || ''} ${user.lastName || ''}`.trim() || user.username}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {user.gender} • {user.age} yaş
                        </Typography>
                      </Box>
                      {user.isVerified && (
                        <Tooltip title="Doğrulanmış Kullanıcı">
                          <CheckCircleIcon fontSize="small" color="success" />
                        </Tooltip>
                      )}
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Typography 
                      variant="body2" 
                      sx={{ 
                        fontFamily: 'monospace',
                        overflow: 'hidden', 
                        textOverflow: 'ellipsis', 
                        whiteSpace: 'nowrap',
                        maxWidth: 120
                      }}
                      title={user.username}
                    >
                      {user.username}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, maxWidth: 190 }}>
                      <EmailIcon fontSize="small" color="action" />
                      <Typography 
                        variant="body2" 
                        sx={{ 
                          overflow: 'hidden', 
                          textOverflow: 'ellipsis', 
                          whiteSpace: 'nowrap',
                          flex: 1
                        }}
                        title={user.email}
                      >
                        {user.email}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, maxWidth: 80 }}>
                      <LocationIcon fontSize="small" color="action" />
                      <Typography 
                        variant="body2"
                        sx={{ 
                          overflow: 'hidden', 
                          textOverflow: 'ellipsis', 
                          whiteSpace: 'nowrap',
                          flex: 1
                        }}
                        title={user.city || '-'}
                      >
                        {user.city || '-'}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                      <StarIcon fontSize="small" color="warning" />
                      <Typography variant="body2" sx={{ fontWeight: 600, color: 'success.main' }}>
                        ₺{user.totalEarnings?.toLocaleString() || '0'}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={user.completedSurveysCount || 0} 
                      color="info" 
                      size="small"
                      sx={{ minWidth: 40 }}
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2">
                      {user.lastActivityDate ? formatDate(user.lastActivityDate) : '-'}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    {getStatusChip(user.accountStatus)}
                  </TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', gap: 0.5 }}>
                      <Tooltip title="Detayları Görüntüle">
                        <IconButton
                          size="small"
                          onClick={() => handleViewDetails(user)}
                          color="primary"
                        >
                          <ViewIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Ayarlar">
                        <IconButton
                          size="small"
                          color="default"
                        >
                          <SettingsIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        
        {/* Enhanced Pagination */}
        <Box sx={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center', 
          p: 2,
          borderTop: '1px solid #e0e0e0'
        }}>
          <Typography variant="body2" color="text.secondary">
            Sayfa {page} / {totalPages} • Toplam {sortedUsers.length} kayıt
          </Typography>
          <Pagination
            count={totalPages}
            page={page}
            onChange={(e, newPage) => setPage(newPage)}
            color="primary"
            showFirstButton
            showLastButton
            size="small"
          />
        </Box>
      </Paper>

      {/* Professional User Detail Modal */}
      <Dialog
        open={detailDialogOpen}
        onClose={() => setDetailDialogOpen(false)}
        maxWidth="lg"
        fullWidth
        PaperProps={{
          sx: { 
            minHeight: '80vh',
            background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)'
          }
        }}
      >
        <DialogTitle sx={{ 
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 
          color: 'white',
          display: 'flex',
          alignItems: 'center',
          gap: 2
        }}>
          <Avatar sx={{ backgroundColor: 'rgba(255,255,255,0.3)' }}>
            <PersonIcon />
          </Avatar>
          <Box>
            <Typography variant="h5" sx={{ fontWeight: 600 }}>
              {selectedUser ? `${selectedUser.firstName} ${selectedUser.lastName}` : 'Kullanıcı Detayları'}
            </Typography>
            <Typography variant="body2" sx={{ opacity: 0.9 }}>
              Professional iField Kullanıcı Profili
            </Typography>
          </Box>
        </DialogTitle>
        
        <DialogContent sx={{ p: 0 }}>
          {selectedUser && (
            <>
              <Tabs 
                value={tabValue} 
                onChange={(e, newValue) => setTabValue(newValue)}
                sx={{ borderBottom: 1, borderColor: 'divider', backgroundColor: 'white' }}
              >
                <Tab 
                  label="Genel Bilgiler" 
                  icon={<PersonIcon />} 
                  iconPosition="start"
                />
                <Tab 
                  label="Anket Geçmişi" 
                  icon={<CalendarIcon />} 
                  iconPosition="start"
                />
                <Tab 
                  label="Güvenlik & IP" 
                  icon={<SecurityIcon />} 
                  iconPosition="start"
                />
              </Tabs>

              {/* Tab 1: General Information */}
              {tabValue === 0 && (
                <Box sx={{ p: 3 }}>
                  <Grid container spacing={3}>
                    {/* Profile Summary */}
                    <Grid item xs={12} md={4}>
                      <Card sx={{ height: '100%', textAlign: 'center', p: 3 }}>
                        <Avatar
                          sx={{
                            width: 120,
                            height: 120,
                            margin: '0 auto 16px',
                            backgroundColor: selectedUser.gender === 'Kadın' ? '#E91E63' : '#2196F3',
                            fontSize: '2rem'
                          }}
                        >
                          {selectedUser.firstName?.charAt(0)}{selectedUser.lastName?.charAt(0)}
                        </Avatar>
                        <Typography variant="h5" sx={{ fontWeight: 600, mb: 1 }}>
                          {selectedUser.firstName} {selectedUser.lastName}
                        </Typography>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                          @{selectedUser.username}
                        </Typography>
                                                 <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1, mb: 2 }}>
                           {getStatusChip(selectedUser.accountStatus)}
                         </Box>
                        {selectedUser.isVerified && (
                          <Chip
                            icon={<CheckCircleIcon />}
                            label="Doğrulanmış Hesap"
                            color="success"
                            variant="outlined"
                          />
                        )}
                        <Box sx={{ mt: 3, p: 2, backgroundColor: '#f5f5f5', borderRadius: 2 }}>
                          <Typography variant="h4" color="primary" sx={{ fontWeight: 600 }}>
                            ₺{selectedUser.totalEarnings?.toLocaleString() || '0'}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            Toplam Kazanç
                          </Typography>
                        </Box>
                      </Card>
                    </Grid>

                    {/* Personal Information */}
                    <Grid item xs={12} md={8}>
                      <Card sx={{ height: '100%', p: 3 }}>
                        <Typography variant="h6" sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 1 }}>
                          <PersonIcon color="primary" />
                          Kişisel Bilgiler
                        </Typography>
                        <Grid container spacing={2}>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Kullanıcı ID</Typography>
                              <Typography variant="body1" sx={{ fontWeight: 600 }}>#{selectedUser.id}</Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">E-posta</Typography>
                              <Typography variant="body1">{selectedUser.email}</Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Telefon</Typography>
                              <Typography variant="body1">{selectedUser.phoneNumber || '-'}</Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Doğum Tarihi</Typography>
                              <Typography variant="body1">{selectedUser.birthDate ? formatDate(selectedUser.birthDate) : '-'}</Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Cinsiyet</Typography>
                              <Typography variant="body1">{selectedUser.gender || '-'}</Typography>
                            </Box>
                          </Grid>
                                                     <Grid item xs={6}>
                             <Box sx={{ mb: 2 }}>
                               <Typography variant="caption" color="text.secondary">Yaş</Typography>
                               <Typography variant="body1">{selectedUser.age || '-'}</Typography>
                             </Box>
                           </Grid>
                          <Grid item xs={12}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Adres</Typography>
                              <Typography variant="body1">
                                {[selectedUser.address1, selectedUser.address2, selectedUser.city, selectedUser.country]
                                  .filter(Boolean).join(', ') || '-'}
                              </Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Kayıt Tarihi</Typography>
                              <Typography variant="body1">{selectedUser.registrationDate ? formatDate(selectedUser.registrationDate) : '-'}</Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Son Giriş</Typography>
                              <Typography variant="body1">{selectedUser.lastLoginDate ? formatDate(selectedUser.lastLoginDate) : '-'}</Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Puan</Typography>
                              <Typography variant="body1" sx={{ fontWeight: 600, color: 'warning.main' }}>
                                {selectedUser.points?.toLocaleString() || '0'}
                              </Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={6}>
                            <Box sx={{ mb: 2 }}>
                              <Typography variant="caption" color="text.secondary">Profil Tamamlanma</Typography>
                              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <Typography variant="body1" sx={{ fontWeight: 600 }}>
                                  %{selectedUser.profileCompleteness || 0}
                                </Typography>
                                <Box sx={{ 
                                  width: 60, 
                                  height: 8, 
                                  backgroundColor: '#e0e0e0', 
                                  borderRadius: 4,
                                  overflow: 'hidden'
                                }}>
                                  <Box sx={{
                                    width: `${selectedUser.profileCompleteness || 0}%`,
                                    height: '100%',
                                    backgroundColor: selectedUser.profileCompleteness && selectedUser.profileCompleteness > 80 ? '#4caf50' : '#ff9800'
                                  }} />
                                </Box>
                              </Box>
                            </Box>
                          </Grid>
                        </Grid>
                      </Card>
                    </Grid>
                  </Grid>
                </Box>
              )}

              {/* Tab 2: Survey History */}
              {tabValue === 1 && (
                <Box sx={{ p: 3 }}>
                  <Grid container spacing={3}>
                    <Grid item xs={12} md={4}>
                      <Card sx={{ p: 3, textAlign: 'center' }}>
                        <Typography variant="h4" color="primary" sx={{ fontWeight: 600 }}>
                          {selectedUser.completedSurveysCount || 0}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          Tamamlanan Anket
                        </Typography>
                      </Card>
                    </Grid>
                    <Grid item xs={12} md={4}>
                      <Card sx={{ p: 3, textAlign: 'center' }}>
                        <Typography variant="h4" color="success.main" sx={{ fontWeight: 600 }}>
                          {selectedUser.averageRating?.toFixed(1) || '0.0'}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          Ortalama Değerlendirme
                        </Typography>
                      </Card>
                    </Grid>
                    <Grid item xs={12} md={4}>
                      <Card sx={{ p: 3, textAlign: 'center' }}>
                        <Typography variant="h4" color="warning.main" sx={{ fontWeight: 600 }}>
                          ₺{selectedUser.totalEarnings?.toLocaleString() || '0'}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          Toplam Kazanç
                        </Typography>
                      </Card>
                    </Grid>
                    <Grid item xs={12}>
                      <Card sx={{ p: 3 }}>
                        <Typography variant="h6" sx={{ mb: 2 }}>
                          Anket Geçmişi
                        </Typography>
                        {selectedUser.completedSurveys && selectedUser.completedSurveys.length > 0 ? (
                          <List>
                            {selectedUser.completedSurveys.map((survey) => (
                              <ListItem key={survey.id} divider>
                                <ListItemIcon>
                                  <CalendarIcon color="primary" />
                                </ListItemIcon>
                                <ListItemText
                                  primary={survey.title}
                                  secondary={
                                    <Box>
                                      <Typography variant="body2" color="text.secondary">
                                        {formatDate(survey.completedAt)} • {survey.location} • {survey.duration} dk
                                      </Typography>
                                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1 }}>
                                        <Chip label={`₺${survey.reward}`} color="success" size="small" />
                                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                          {[...Array(5)].map((_, i) => (
                                            <StarIcon
                                              key={i}
                                              fontSize="small"
                                              sx={{ 
                                                color: i < (survey.rating || 0) ? '#ffc107' : '#e0e0e0' 
                                              }}
                                            />
                                          ))}
                                        </Box>
                                      </Box>
                                    </Box>
                                  }
                                />
                              </ListItem>
                            ))}
                          </List>
                        ) : (
                          <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
                            Henüz tamamlanmış anket bulunmuyor
                          </Typography>
                        )}
                      </Card>
                    </Grid>
                  </Grid>
                </Box>
              )}

              {/* Tab 3: Security & IP */}
              {tabValue === 2 && (
                <Box sx={{ p: 3 }}>
                  <Grid container spacing={3}>
                    <Grid item xs={12} md={6}>
                      <Card sx={{ p: 3 }}>
                        <Typography variant="h6" sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
                          <SecurityIcon color="primary" />
                          Güvenlik Bilgileri
                        </Typography>
                        
                        <Box sx={{ mb: 2 }}>
                          <Typography variant="caption" color="text.secondary">IP Adresi</Typography>
                          <Typography variant="body1" sx={{ fontFamily: 'monospace' }}>
                            {selectedUser.ipAddress || '-'}
                          </Typography>
                        </Box>
                        <Box sx={{ mb: 2 }}>
                          <Typography variant="caption" color="text.secondary">Son Aktivite</Typography>
                          <Typography variant="body1">
                            {selectedUser.lastActivityDate ? formatDate(selectedUser.lastActivityDate) : '-'}
                          </Typography>
                        </Box>
                        <Box sx={{ mb: 2 }}>
                          <Typography variant="caption" color="text.secondary">Hesap Durumu</Typography>
                          <Box sx={{ mt: 0.5 }}>
                            {getStatusChip(selectedUser.accountStatus)}
                          </Box>
                        </Box>
                      </Card>
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <Card sx={{ p: 3 }}>
                        <Typography variant="h6" sx={{ mb: 2 }}>
                          Hesap Yönetimi
                        </Typography>
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                          <Button
                            variant="contained"
                            color="success"
                            onClick={() => handleAccountStatusChange('Aktif')}
                            disabled={selectedUser.accountStatus === 'Aktif'}
                            startIcon={<CheckCircleIcon />}
                          >
                            Hesabı Aktif Yap
                          </Button>
                          <Button
                            variant="contained"
                            color="warning"
                            onClick={() => handleAccountStatusChange('İnceleme Altında')}
                            disabled={selectedUser.accountStatus === 'İnceleme Altında'}
                            startIcon={<WarningIcon />}
                          >
                            İncelemeye Al
                          </Button>
                          <Button
                            variant="contained"
                            color="error"
                            onClick={() => handleAccountStatusChange('Banlı')}
                            disabled={selectedUser.accountStatus === 'Banlı'}
                            startIcon={<BlockIcon />}
                          >
                            Hesabı Banla
                          </Button>
                        </Box>
                      </Card>
                    </Grid>
                    <Grid item xs={12}>
                      <Card sx={{ p: 3 }}>
                        <Typography variant="h6" sx={{ mb: 2 }}>
                          Güvenlik Notları
                        </Typography>
                        {selectedUser.securityNotes && selectedUser.securityNotes.length > 0 ? (
                          <List>
                            {selectedUser.securityNotes.map((note, index) => (
                              <ListItem key={index}>
                                <ListItemIcon>
                                  <SecurityIcon fontSize="small" />
                                </ListItemIcon>
                                <ListItemText primary={note} />
                              </ListItem>
                            ))}
                          </List>
                        ) : (
                          <Typography variant="body2" color="text.secondary">
                            Güvenlik notu bulunmuyor
                          </Typography>
                        )}
                      </Card>
                    </Grid>
                  </Grid>
                </Box>
              )}
            </>
          )}
        </DialogContent>
        
        <DialogActions sx={{ p: 3, backgroundColor: 'white' }}>
          <Button onClick={() => setDetailDialogOpen(false)} variant="outlined">
            Kapat
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for notifications */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert 
          onClose={() => setSnackbar({ ...snackbar, open: false })} 
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

const UserManagementWithErrorBoundary = () => (
  <ErrorBoundary>
    <UserManagement />
  </ErrorBoundary>
);

export default UserManagementWithErrorBoundary; 