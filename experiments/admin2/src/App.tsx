import React, { useState, useEffect } from 'react';
import { AppBar, Box, Toolbar, Typography, Container, IconButton, Menu, MenuItem, Grid } from '@mui/material';
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import SurveyManagement from './pages/SurveyManagement';
import PaymentRequests from './pages/PaymentRequests';
import SecuritySettings from './pages/SecuritySettings';
import Login from './pages/Login';
import HomeIcon from '@mui/icons-material/Home';
import AssignmentIcon from '@mui/icons-material/Assignment';
import SettingsIcon from '@mui/icons-material/Settings';
import GroupIcon from '@mui/icons-material/Group';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import SecurityIcon from '@mui/icons-material/Security';
import UserManagement from './pages/UserManagement';
import ArticleIcon from '@mui/icons-material/Article';
import StoryManagement from './pages/StoryManagement';
import NotificationManagement from './pages/NotificationManagement';
import NotificationsIcon from '@mui/icons-material/Notifications';

const menuConfig = [
  {
    label: 'ANASAYFA',
    icon: <HomeIcon fontSize="large" />,
    path: '/',
    submenu: [],
  },
  {
    label: 'YÖNETİM',
    icon: <SettingsIcon fontSize="large" />,
    submenu: [
      { label: 'Kullanıcı Yönetimi', icon: <GroupIcon />, path: '/users' },
      { label: 'Bildirim Yönetimi', icon: <NotificationsIcon />, path: '/notifications' },
      { label: 'Ayarlar', icon: <SettingsIcon />, path: '/settings' },
    ],
  },
  {
    label: 'ANKETLER',
    icon: <AssignmentIcon fontSize="large" />,
    submenu: [
      { label: 'Anket Yönetimi', icon: <AssignmentIcon />, path: '/surveys' },
      { label: 'Hikaye Yönetimi', icon: <ArticleIcon />, path: '/stories' },
      { label: 'Ödeme İstekleri', icon: <AssignmentIcon />, path: '/payments' },
    ],
  },
  {
    label: 'GÜVENLİK',
    icon: <SecurityIcon fontSize="large" />,
    submenu: [
      { label: 'Güvenlik Ayarları', icon: <SecurityIcon />, path: '/security' },
    ],
  },
  {
    label: 'ÇIKIŞ',
    icon: <ExitToAppIcon fontSize="large" />,
    path: '/logout',
    submenu: [],
  },
];

const TopBar = ({ userEmail }: { userEmail?: string }) => (
  <AppBar position="static" sx={{ bgcolor: '#1976d2', height: 48, boxShadow: 0, justifyContent: 'center' }}>
    <Container maxWidth={false} disableGutters>
      <Toolbar sx={{ minHeight: 48, px: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Box sx={{ width: 32, height: 32, bgcolor: '#fff', borderRadius: '50%', mr: 2 }} />
          <Typography variant="h6" sx={{ fontWeight: 700, color: '#fff', fontSize: 20, letterSpacing: 1 }}>
            KazaniOn Admin Panel
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography variant="body2" sx={{ color: '#fff', opacity: 0.9 }}>
            PostgreSQL - KVKK Uyumlu
          </Typography>
          {userEmail && (
            <Typography variant="body2" sx={{ color: '#fff', fontWeight: 500 }}>
              {userEmail}
            </Typography>
          )}
        </Box>
      </Toolbar>
    </Container>
  </AppBar>
);

const MainMenu = ({ onLogout }: { onLogout: () => void }) => {
  const location = useLocation();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [openIdx, setOpenIdx] = useState<number | null>(null);

  const handleMenuOpen = (idx: number, event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
    setOpenIdx(idx);
  };
  const handleMenuClose = () => {
    setAnchorEl(null);
    setOpenIdx(null);
  };

  return (
    <AppBar position="static" sx={{ bgcolor: '#23272b', boxShadow: 1, minHeight: 72, justifyContent: 'center' }}>
      <Container maxWidth={false} disableGutters>
        <Toolbar sx={{ minHeight: 72, px: 2, pl: 0, alignItems: 'flex-end' }}>
          <Grid container spacing={0} alignItems="flex-end" sx={{ flexWrap: 'nowrap', width: 'auto' }}>
            {menuConfig.map((item, idx) => (
              <Grid item key={item.label} sx={{ mr: 2 }}>
                {item.label === 'ÇIKIŞ' ? (
                  <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', cursor: 'pointer' }} onClick={onLogout}>
                    <IconButton sx={{ color: '#fff', mb: 0.5 }}>{item.icon}</IconButton>
                    <Typography sx={{ color: '#fff', fontSize: 13, fontWeight: 700, letterSpacing: 0.5 }}>
                      {item.label}
                    </Typography>
                  </Box>
                ) : item.submenu && item.submenu.length > 0 ? (
                  <Box
                    sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', cursor: 'pointer', position: 'relative' }}
                    onMouseEnter={e => handleMenuOpen(idx, e as any)}
                    onMouseLeave={handleMenuClose}
                  >
                    <IconButton sx={{ color: '#fff', mb: 0.5 }}>{item.icon}</IconButton>
                    <Typography sx={{ color: '#fff', fontSize: 13, fontWeight: 700, letterSpacing: 0.5 }}>
                      {item.label}
                    </Typography>
                    <Menu
                      anchorEl={anchorEl}
                      open={openIdx === idx}
                      onClose={handleMenuClose}
                      anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                      transformOrigin={{ vertical: 'top', horizontal: 'left' }}
                      MenuListProps={{ onMouseLeave: handleMenuClose }}
                      PaperProps={{ sx: { mt: 1, minWidth: 220, bgcolor: '#fff', color: '#23272b', boxShadow: 3 } }}
                    >
                      {item.submenu.map(sub => (
                        <MenuItem
                          key={sub.label}
                          component={Link}
                          to={sub.path}
                          onClick={handleMenuClose}
                          sx={{ display: 'flex', alignItems: 'center', gap: 1, fontWeight: 500 }}
                        >
                          {sub.icon}
                          {sub.label}
                        </MenuItem>
                      ))}
                    </Menu>
                  </Box>
                ) : (
                  <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', cursor: 'pointer' }}>
                    {item.path ? (
                      <Link to={item.path} style={{ textDecoration: 'none' }}>
                        <IconButton sx={{ color: '#fff', mb: 0.5 }}>
                          {item.icon}
                        </IconButton>
                      </Link>
                    ) : (
                      <IconButton sx={{ color: '#fff', mb: 0.5 }}>
                      {item.icon}
                    </IconButton>
                    )}
                    <Typography
                      sx={{
                        color: '#fff',
                        fontSize: 13,
                        fontWeight: 700,
                        letterSpacing: 0.5,
                        borderBottom: location.pathname === item.path ? '3px solid #1976d2' : '3px solid transparent',
                        pb: 0.5,
                      }}
                    >
                      {item.label}
                    </Typography>
                  </Box>
                )}
              </Grid>
            ))}
          </Grid>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

const App = () => {
  const [loggedIn, setLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState<string>('');
  const [loading, setLoading] = useState(true);

  // Check localStorage for existing login session
  useEffect(() => {
    const isLoggedIn = localStorage.getItem('kazanion-admin-logged-in') === 'true';
    const storedEmail = localStorage.getItem('kazanion-admin-user') || '';
    
    if (isLoggedIn && storedEmail) {
      setLoggedIn(true);
      setUserEmail(storedEmail);
    }
    setLoading(false);
  }, []);

  const handleLogin = () => {
    const storedEmail = localStorage.getItem('kazanion-admin-user') || '';
    setLoggedIn(true);
    setUserEmail(storedEmail);
  };

  const handleLogout = () => {
    // Clear localStorage
    localStorage.removeItem('kazanion-admin-logged-in');
    localStorage.removeItem('kazanion-admin-user');
    
    setLoggedIn(false);
    setUserEmail('');
  };

  // Show loading during initial auth check
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <Typography>Yükleniyor...</Typography>
      </Box>
    );
  }

  return (
    <Router>
      {!loggedIn ? (
        <Login onLogin={handleLogin} />
      ) : (
        <Box>
          <TopBar userEmail={userEmail} />
          <MainMenu onLogout={handleLogout} />
          <Container maxWidth="xl" sx={{ mt: 4 }}>
            <Routes>
              <Route path="/" element={
                <Box sx={{ textAlign: 'center', mt: 6 }}>
                  <Typography variant="h4" sx={{ color: '#1976d2', mb: 2 }}>
                    KazaniOn Admin Paneline Hoş Geldiniz
                  </Typography>
                  <Typography variant="h6" sx={{ color: 'text.secondary', mb: 1 }}>
                    PostgreSQL Backend - KVKK Uyumlu
                  </Typography>
                  <Typography variant="body1" sx={{ color: 'text.secondary' }}>
                    Admin: {userEmail}
                  </Typography>
                </Box>
              } />
              <Route path="/surveys" element={<SurveyManagement />} />
              <Route path="/stories" element={<StoryManagement />} />
              <Route path="/payments" element={<PaymentRequests />} />
              <Route path="/security" element={<SecuritySettings />} />
              <Route path="/logout" element={<Dashboard />} />
              <Route path="/users" element={<UserManagement />} />
              <Route path="/notifications" element={<NotificationManagement />} />
              <Route path="/settings" element={<Typography variant="h6">Ayarlar (Demo)</Typography>} />
            </Routes>
          </Container>
        </Box>
      )}
    </Router>
  );
};

export default App;