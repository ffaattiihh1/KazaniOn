import React from 'react';
import { Box, Grid, Paper, Typography } from '@mui/material';
import { styled } from '@mui/material/styles';

const Item = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(2),
  textAlign: 'center',
  color: theme.palette.text.secondary,
  height: '100%',
  cursor: 'pointer',
  '&:hover': {
    backgroundColor: theme.palette.action.hover,
  },
}));

const Dashboard = () => {
  return (
    <Box sx={{ flexGrow: 1, p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Admin Dashboard
      </Typography>
      
      <Grid container spacing={3}>
        <Grid item xs={12} md={6} lg={4}>
          <Item>
            <Typography variant="h6">Anket Yönetimi</Typography>
            <Typography variant="body2">
              Yeni anket ekle, mevcut anketleri düzenle ve onayla
            </Typography>
          </Item>
        </Grid>

        <Grid item xs={12} md={6} lg={4}>
          <Item>
            <Typography variant="h6">Ödeme İstekleri</Typography>
            <Typography variant="body2">
              Para çekme isteklerini incele ve onayla
            </Typography>
          </Item>
        </Grid>

        <Grid item xs={12} md={6} lg={4}>
          <Item>
            <Typography variant="h6">Güvenlik Kontrolleri</Typography>
            <Typography variant="body2">
              IP ve cihaz kontrollerini yönet
            </Typography>
          </Item>
        </Grid>

        <Grid item xs={12} md={6} lg={4}>
          <Item>
            <Typography variant="h6">Kullanıcı Yönetimi</Typography>
            <Typography variant="body2">
              Kullanıcıları görüntüle ve yönet
            </Typography>
          </Item>
        </Grid>

        <Grid item xs={12} md={6} lg={4}>
          <Item>
            <Typography variant="h6">Raporlar</Typography>
            <Typography variant="body2">
              Anket ve ödeme istatistiklerini görüntüle
            </Typography>
          </Item>
        </Grid>

        <Grid item xs={12} md={6} lg={4}>
          <Item>
            <Typography variant="h6">Ayarlar</Typography>
            <Typography variant="body2">
              Sistem ayarlarını yapılandır
            </Typography>
          </Item>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard; 