import React, { useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
  Security as SecurityIcon,
  Timeline as TimelineIcon,
} from '@mui/icons-material';

interface PaymentRequest {
  id: string;
  userId: string;
  userName: string;
  amount: number;
  status: 'pending' | 'approved' | 'rejected';
  createdAt: Date;
  securityChecks: {
    ipCheck: boolean;
    deviceCheck: boolean;
    timeCheck: boolean;
    behaviorCheck: boolean;
  };
  surveyHistory: {
    surveyId: string;
    surveyTitle: string;
    completedAt: Date;
  }[];
}

const PaymentRequests = () => {
  const [requests, setRequests] = useState<PaymentRequest[]>([
    {
      id: '1',
      userId: 'user1',
      userName: 'Ahmet Yılmaz',
      amount: 150,
      status: 'pending',
      createdAt: new Date(),
      securityChecks: {
        ipCheck: true,
        deviceCheck: true,
        timeCheck: false,
        behaviorCheck: true,
      },
      surveyHistory: [
        {
          surveyId: 'survey1',
          surveyTitle: 'Alışveriş Alışkanlıkları',
          completedAt: new Date(Date.now() - 86400000), // 1 day ago
        },
        {
          surveyId: 'survey2',
          surveyTitle: 'Mobil Uygulama Kullanımı',
          completedAt: new Date(Date.now() - 172800000), // 2 days ago
        },
      ],
    },
  ]);

  const [selectedRequest, setSelectedRequest] = useState<PaymentRequest | null>(null);
  const [openDialog, setOpenDialog] = useState(false);

  const handleApprove = (id: string) => {
    setRequests(requests.map(request =>
      request.id === id ? { ...request, status: 'approved' } : request
    ));
  };

  const handleReject = (id: string) => {
    setRequests(requests.map(request =>
      request.id === id ? { ...request, status: 'rejected' } : request
    ));
  };

  const handleViewDetails = (request: PaymentRequest) => {
    setSelectedRequest(request);
    setOpenDialog(true);
  };

  const getSecurityStatus = (checks: PaymentRequest['securityChecks']) => {
    const allChecks = Object.values(checks);
    const passedChecks = allChecks.filter(check => check).length;
    return (passedChecks / allChecks.length) * 100;
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Ödeme İstekleri
      </Typography>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Kullanıcı</TableCell>
              <TableCell>Tutar</TableCell>
              <TableCell>Durum</TableCell>
              <TableCell>Güvenlik Skoru</TableCell>
              <TableCell>İstek Tarihi</TableCell>
              <TableCell>İşlemler</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {requests.map((request) => (
              <TableRow key={request.id}>
                <TableCell>{request.userName}</TableCell>
                <TableCell>{request.amount} TL</TableCell>
                <TableCell>
                  <Chip
                    label={request.status}
                    color={
                      request.status === 'approved'
                        ? 'success'
                        : request.status === 'rejected'
                        ? 'error'
                        : 'warning'
                    }
                  />
                </TableCell>
                <TableCell>
                  <Tooltip title="Güvenlik Detayları">
                    <Chip
                      icon={<SecurityIcon />}
                      label={`${getSecurityStatus(request.securityChecks)}%`}
                      color={
                        getSecurityStatus(request.securityChecks) > 75
                          ? 'success'
                          : getSecurityStatus(request.securityChecks) > 50
                          ? 'warning'
                          : 'error'
                      }
                    />
                  </Tooltip>
                </TableCell>
                <TableCell>{request.createdAt.toLocaleDateString()}</TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Tooltip title="Detayları Görüntüle">
                      <IconButton
                        color="primary"
                        onClick={() => handleViewDetails(request)}
                      >
                        <TimelineIcon />
                      </IconButton>
                    </Tooltip>
                    {request.status === 'pending' && (
                      <>
                        <Tooltip title="Onayla">
                          <IconButton
                            color="success"
                            onClick={() => handleApprove(request.id)}
                          >
                            <CheckCircleIcon />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Reddet">
                          <IconButton
                            color="error"
                            onClick={() => handleReject(request.id)}
                          >
                            <CancelIcon />
                          </IconButton>
                        </Tooltip>
                      </>
                    )}
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        maxWidth="md"
        fullWidth
      >
        {selectedRequest && (
          <>
            <DialogTitle>Ödeme İsteği Detayları</DialogTitle>
            <DialogContent>
              <Box sx={{ mt: 2 }}>
                <Typography variant="h6" gutterBottom>
                  Kullanıcı Bilgileri
                </Typography>
                <Typography>
                  Kullanıcı ID: {selectedRequest.userId}
                </Typography>
                <Typography>
                  Kullanıcı Adı: {selectedRequest.userName}
                </Typography>
                <Typography>
                  İstenen Tutar: {selectedRequest.amount} TL
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }} gutterBottom>
                  Güvenlik Kontrolleri
                </Typography>
                <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                  <Chip
                    label="IP Kontrolü"
                    color={selectedRequest.securityChecks.ipCheck ? 'success' : 'error'}
                  />
                  <Chip
                    label="Cihaz Kontrolü"
                    color={selectedRequest.securityChecks.deviceCheck ? 'success' : 'error'}
                  />
                  <Chip
                    label="Zaman Kontrolü"
                    color={selectedRequest.securityChecks.timeCheck ? 'success' : 'error'}
                  />
                  <Chip
                    label="Davranış Kontrolü"
                    color={selectedRequest.securityChecks.behaviorCheck ? 'success' : 'error'}
                  />
                </Box>

                <Typography variant="h6" sx={{ mt: 3 }} gutterBottom>
                  Anket Geçmişi
                </Typography>
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Anket</TableCell>
                        <TableCell>Tamamlanma Tarihi</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedRequest.surveyHistory.map((survey) => (
                        <TableRow key={survey.surveyId}>
                          <TableCell>{survey.surveyTitle}</TableCell>
                          <TableCell>
                            {survey.completedAt.toLocaleDateString()}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Box>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenDialog(false)}>Kapat</Button>
              {selectedRequest.status === 'pending' && (
                <>
                  <Button
                    onClick={() => {
                      handleApprove(selectedRequest.id);
                      setOpenDialog(false);
                    }}
                    color="success"
                    variant="contained"
                  >
                    Onayla
                  </Button>
                  <Button
                    onClick={() => {
                      handleReject(selectedRequest.id);
                      setOpenDialog(false);
                    }}
                    color="error"
                    variant="contained"
                  >
                    Reddet
                  </Button>
                </>
              )}
            </DialogActions>
          </>
        )}
      </Dialog>
    </Box>
  );
};

export default PaymentRequests; 