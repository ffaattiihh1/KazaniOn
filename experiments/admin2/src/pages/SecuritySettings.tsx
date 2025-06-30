import React, { useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  Switch,
  FormControlLabel,
  Button,
  Alert,
  Snackbar,
} from '@mui/material';

interface SecuritySettingsProps {}

const SecuritySettings: React.FC<SecuritySettingsProps> = () => {
  const [settings, setSettings] = useState({
    ipCheck: true,
    deviceCheck: true,
    timeCheck: true,
    behaviorCheck: true,
  });

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success' as 'success' | 'error',
  });

  const handleChange = (setting: keyof typeof settings) => {
    setSettings(prev => ({
      ...prev,
      [setting]: !prev[setting],
    }));
  };

  const handleSave = () => {
    // Burada ayarları kaydetme işlemi yapılacak
    setSnackbar({
      open: true,
      message: 'Ayarlar başarıyla kaydedildi',
      severity: 'success',
    });
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Güvenlik Ayarları
      </Typography>

      <Paper sx={{ p: 3, mb: 3 }}>
            <Typography variant="h6" gutterBottom>
          Otomatik Kontroller
            </Typography>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <FormControlLabel
              control={
                <Switch
                checked={settings.ipCheck}
                onChange={() => handleChange('ipCheck')}
                />
              }
            label="IP Adresi Kontrolü"
          />
            <FormControlLabel
              control={
                <Switch
                checked={settings.deviceCheck}
                onChange={() => handleChange('deviceCheck')}
                />
              }
            label="Cihaz Kontrolü"
            />
            <FormControlLabel
              control={
                <Switch
                checked={settings.timeCheck}
                onChange={() => handleChange('timeCheck')}
                />
              }
            label="Zaman Kontrolü"
            />
            <FormControlLabel
              control={
                <Switch
                checked={settings.behaviorCheck}
                onChange={() => handleChange('behaviorCheck')}
                />
              }
            label="Davranış Analizi"
              />
            </Box>

        <Box sx={{ mt: 3 }}>
          <Button
            variant="contained"
            color="primary"
            onClick={handleSave}
          >
          Ayarları Kaydet
        </Button>
      </Box>
      </Paper>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
      >
        <Alert
          onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
          severity={snackbar.severity}
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default SecuritySettings; 