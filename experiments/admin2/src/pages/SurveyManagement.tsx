import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Button,
  TextField,
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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
} from '@mui/material';
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
// Firebase imports removed for KVKK compliance - using PostgreSQL API instead

// Fix for default marker icon
// @ts-ignore
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

interface Survey {
  id: number; // Changed from string to number for PostgreSQL
  title: string;
  description?: string;
  points: number; // Changed from reward to match backend
  link?: string;
  locationBased: boolean; // Changed from type to match backend
  latitude?: number | null;
  longitude?: number | null;
  isActive: boolean; // Changed from status to match backend
  createdAt: string; // Backend returns ISO string
}

const API_BASE_URL = 'https://kazanion.onrender.com/api';

const SurveyManagement = () => {
  const [surveys, setSurveys] = useState<Survey[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [newSurvey, setNewSurvey] = useState<Partial<Survey>>({
    locationBased: true,
    isActive: true,
  });
  const [selectedLocation, setSelectedLocation] = useState<[number, number] | null>(null);
  const [loading, setLoading] = useState(true);
  const [editSurvey, setEditSurvey] = useState<Survey | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // PostgreSQL API'den anketleri çek
  useEffect(() => {
    fetchSurveys();
  }, []);

  const fetchSurveys = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/polls`);
      const data = await response.json();
      setSurveys(data);
      setLoading(false);
    } catch (error) {
      console.error('Anketler yüklenirken hata:', error);
      setError('Anketler yüklenemedi');
      setLoading(false);
    }
  };

  // Anket ekle - SADECE PostgreSQL'e
  const handleAddSurvey = async () => {
    if (newSurvey.locationBased && !selectedLocation) {
      setError('Konumlu anketler için haritadan bir konum seçmelisiniz!');
      return;
    }
    if (!newSurvey.title || !newSurvey.points) {
      setError('Anket eklenemedi: Başlık veya ödül miktarı eksik');
      return;
    }

    setError(null);
    
    const surveyData = {
      title: newSurvey.title,
      description: newSurvey.description || '',
      points: Number(newSurvey.points),
      locationBased: newSurvey.locationBased || false,
      latitude: selectedLocation ? selectedLocation[0] : null,
      longitude: selectedLocation ? selectedLocation[1] : null,
      link: newSurvey.link || null,
      isActive: true,
    };

    try {
      const response = await fetch(`${API_BASE_URL}/polls`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify(surveyData),
      });

      if (response.ok) {
        await fetchSurveys(); // Listeyi yenile
        setOpenDialog(false);
        setNewSurvey({ locationBased: true, isActive: true });
        setSelectedLocation(null);
      } else {
        throw new Error('Anket eklenirken hata oluştu');
      }
    } catch (error) {
      console.error('Anket eklenirken hata:', error);
      setError('Anket eklenirken hata oluştu');
    }
  };

  // Anketi sil
  const handleDeleteSurvey = async (id: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/polls/${id}`, {
        method: 'DELETE',
      });
      
      if (response.ok) {
        await fetchSurveys(); // Listeyi yenile
      } else {
        throw new Error('Anket silinirken hata oluştu');
      }
    } catch (error) {
      console.error('Anket silinirken hata:', error);
      setError('Anket silinirken hata oluştu');
    }
  };

  // Anketi düzenle
  const handleEditSurvey = (survey: Survey) => {
    setEditSurvey(survey);
    setEditDialogOpen(true);
  };

  const handleEditSurveySave = async () => {
    if (!editSurvey) return;

    const updateData = {
      title: editSurvey.title,
      description: editSurvey.description || '',
      points: editSurvey.points,
      locationBased: editSurvey.locationBased,
      latitude: editSurvey.latitude,
      longitude: editSurvey.longitude,
      link: editSurvey.link || null,
      isActive: editSurvey.isActive,
    };

    try {
      const response = await fetch(`${API_BASE_URL}/polls/${editSurvey.id}`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify(updateData),
      });

      if (response.ok) {
        await fetchSurveys(); // Listeyi yenile
        setEditDialogOpen(false);
        setEditSurvey(null);
      } else {
        throw new Error('Anket güncellenirken hata oluştu');
      }
    } catch (error) {
      console.error('Anket güncellenirken hata:', error);
      setError('Anket güncellenirken hata oluştu');
    }
  };

  const MapEvents = () => {
    useMapEvents({
      click: (e) => {
        setSelectedLocation([e.latlng.lat, e.latlng.lng]);
      },
    });
    return null;
  };

  const EditMapEvents = ({ setEditSurvey }: { setEditSurvey: (s: any) => void }) => {
    useMapEvents({
      click: (e) => {
        setEditSurvey((prev: any) => ({ ...prev, latitude: e.latlng.lat, longitude: e.latlng.lng }));
      },
    });
    return null;
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">Anket Yönetimi</Typography>
        <Button variant="contained" onClick={() => setOpenDialog(true)}>
          Yeni Anket Ekle
        </Button>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Başlık</TableCell>
                <TableCell>Tür</TableCell>
                <TableCell>Ödül</TableCell>
                <TableCell>Durum</TableCell>
                <TableCell>Oluşturulma Tarihi</TableCell>
                <TableCell>İşlemler</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {surveys.map((survey) => (
                <TableRow key={survey.id}>
                  <TableCell>{survey.title}</TableCell>
                  <TableCell>{survey.locationBased ? 'Konumlu' : 'Link'}</TableCell>
                  <TableCell>{survey.points} TL</TableCell>
                  <TableCell>{survey.isActive ? 'Aktif' : 'Pasif'}</TableCell>
                  <TableCell>{new Date(survey.createdAt).toLocaleDateString()}</TableCell>
                  <TableCell>
                    <Button
                      variant="outlined"
                      color="primary"
                      onClick={() => handleEditSurvey(survey)}
                      style={{ marginLeft: 8 }}
                    >
                      Düzenle
                    </Button>
                    <Button
                      variant="outlined"
                      color="error"
                      onClick={() => handleDeleteSurvey(survey.id)}
                      style={{ marginLeft: 8 }}
                    >
                      Sil
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Yeni Anket Ekle</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            {error && <Typography color="error" sx={{ mb: 2 }}>{error}</Typography>}
            <TextField
              label="Başlık"
              fullWidth
              margin="normal"
              value={newSurvey.title || ''}
              onChange={e => setNewSurvey({ ...newSurvey, title: e.target.value })}
            />
            <TextField
              label="Açıklama"
              fullWidth
              margin="normal"
              value={newSurvey.description || ''}
              onChange={e => setNewSurvey({ ...newSurvey, description: e.target.value })}
              helperText="Başlıkları renklendirmek için [blue]Başlık[/blue], [red]Uyarı[/red] gibi tagler kullanabilirsin. İçerik siyah olur."
            />
            <FormControl fullWidth margin="normal">
              <InputLabel>Tür</InputLabel>
              <Select
                value={newSurvey.locationBased ? 'location' : 'link'}
                label="Tür"
                onChange={e => setNewSurvey({ ...newSurvey, locationBased: e.target.value === 'location' })}
              >
                <MenuItem value="location">Konumlu</MenuItem>
                <MenuItem value="link">Link</MenuItem>
              </Select>
            </FormControl>

            {!newSurvey.locationBased && (
              <TextField
                fullWidth
                label="Anket Linki"
                value={newSurvey.link || ''}
                onChange={(e) => setNewSurvey({ ...newSurvey, link: e.target.value })}
                sx={{ mb: 2 }}
              />
            )}

            {newSurvey.locationBased && (
              <Box sx={{ height: 400, mb: 2 }}>
                <MapContainer
                  center={[41.0082, 28.9784]} // Istanbul coordinates
                  zoom={13}
                  style={{ height: '100%', width: '100%' }}
                >
                  <TileLayer
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                  />
                  <MapEvents />
                  {selectedLocation && (
                    <Marker position={selectedLocation} />
                  )}
                </MapContainer>
              </Box>
            )}

            <TextField
              fullWidth
              type="number"
              label="Ödül Miktarı (TL)"
              value={newSurvey.points || ''}
              onChange={(e) => setNewSurvey({ ...newSurvey, points: Number(e.target.value) })}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>İptal</Button>
          <Button onClick={handleAddSurvey} variant="contained" disabled={newSurvey.locationBased && !selectedLocation}>
            Ekle
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Anketi Düzenle</DialogTitle>
        <DialogContent>
          {editSurvey && (
            <Box sx={{ mt: 2 }}>
              <TextField
                fullWidth
                label="Anket Başlığı"
                value={editSurvey.title}
                onChange={(e) => setEditSurvey({ ...editSurvey, title: e.target.value })}
                sx={{ mb: 2 }}
              />
              <TextField
                fullWidth
                label="Açıklama"
                value={editSurvey.description || ''}
                onChange={(e) => setEditSurvey({ ...editSurvey, description: e.target.value })}
                sx={{ mb: 2 }}
              />
              <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel>Anket Türü</InputLabel>
                <Select
                  value={editSurvey.locationBased ? 'location' : 'link'}
                  label="Anket Türü"
                  onChange={(e) => setEditSurvey({ ...editSurvey, locationBased: e.target.value === 'location' })}
                >
                  <MenuItem value="location">Konumlu</MenuItem>
                  <MenuItem value="link">Link</MenuItem>
                </Select>
              </FormControl>
              {!editSurvey.locationBased && (
                <TextField
                  fullWidth
                  label="Anket Linki"
                  value={editSurvey.link || ''}
                  onChange={(e) => setEditSurvey({ ...editSurvey, link: e.target.value })}
                  sx={{ mb: 2 }}
                />
              )}
              {editSurvey.locationBased && (
                <Box sx={{ height: 300, mb: 2 }}>
                  <MapContainer
                    center={editSurvey.latitude && editSurvey.longitude ? [editSurvey.latitude, editSurvey.longitude] : [41.0082, 28.9784]}
                    zoom={13}
                    style={{ height: '100%', width: '100%' }}
                  >
                    <TileLayer
                      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                      attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    />
                    <EditMapEvents setEditSurvey={setEditSurvey} />
                    {editSurvey.latitude && editSurvey.longitude && <Marker position={[editSurvey.latitude, editSurvey.longitude]} />}
                  </MapContainer>
                </Box>
              )}
              <TextField
                fullWidth
                type="number"
                label="Ödül Miktarı (TL)"
                value={editSurvey.points}
                onChange={(e) => setEditSurvey({ ...editSurvey, points: Number(e.target.value) })}
              />
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>İptal</Button>
          <Button onClick={handleEditSurveySave} variant="contained">
            Kaydet
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default SurveyManagement; 