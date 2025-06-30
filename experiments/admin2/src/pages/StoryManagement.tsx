import React, { useState, useEffect } from 'react';
// Firebase imports removed for KVKK compliance - using PostgreSQL API only
import {
  Box, Button, Typography, TextField, Dialog, DialogActions,
  DialogContent, DialogTitle, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, IconButton
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

const API_BASE_URL = 'https://kazanion.onrender.com/api';

const StoryManagement = () => {
  const [stories, setStories] = useState<any[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [newStory, setNewStory] = useState({ title: '', description: '', imageUrl: '' });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchStories();
  }, []);

  const fetchStories = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/stories`);
      const data = await response.json();
      setStories(data);
    } catch (error) {
      console.error('Hikayeler çekilirken hata oluştu:', error);
      setError('Hikayeler yüklenirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const handleAddStory = async () => {
    if (!newStory.title || !newStory.imageUrl) {
      setError('Hikaye eklenemedi: Başlık veya resim URL\'si eksik');
      return;
    }
    
    setError(null);
    setLoading(true);
    
    try {
      const response = await fetch(`${API_BASE_URL}/stories`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({
          title: newStory.title,
          description: newStory.description || '',
          imageUrl: newStory.imageUrl,
        }),
      });

      if (response.ok) {
        setOpenDialog(false);
        setNewStory({ title: '', description: '', imageUrl: '' });
        await fetchStories();
      } else {
        throw new Error('Hikaye eklenirken hata oluştu');
      }
    } catch (error) {
      console.error('Hikaye eklenirken hata:', error);
      setError('Hikaye eklenirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };
  
  const handleDeleteStory = async (id: string) => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/stories/${id}`, {
        method: 'DELETE',
      });
      
      if (response.ok) {
        await fetchStories();
      } else {
        throw new Error('Hikaye silinirken hata oluştu');
      }
    } catch (error) {
      console.error('Hikaye silinirken hata:', error);
      setError('Hikaye silinirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Hikaye Yönetimi</Typography>
        <Button 
          variant="contained" 
          onClick={() => setOpenDialog(true)}
          disabled={loading}
        >
          Yeni Hikaye Ekle
        </Button>
      </Box>

      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Yeni Hikaye Ekle</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Başlık"
            fullWidth
            value={newStory.title}
            onChange={(e) => setNewStory({ ...newStory, title: e.target.value })}
            variant="outlined"
          />
          <TextField
            margin="dense"
            label="Açıklama"
            fullWidth
            multiline
            rows={3}
            value={newStory.description}
            onChange={(e) => setNewStory({ ...newStory, description: e.target.value })}
            variant="outlined"
          />
          <TextField
            margin="dense"
            label="Resim URL"
            fullWidth
            value={newStory.imageUrl}
            onChange={(e) => setNewStory({ ...newStory, imageUrl: e.target.value })}
            variant="outlined"
            placeholder="https://example.com/image.jpg"
          />
          {error && <Typography color="error" sx={{ mt: 1 }}>{error}</Typography>}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)} disabled={loading}>
            İptal
          </Button>
          <Button onClick={handleAddStory} variant="contained" disabled={loading}>
            {loading ? 'Ekleniyor...' : 'Ekle'}
          </Button>
        </DialogActions>
      </Dialog>

      <TableContainer component={Paper} sx={{ mt: 2 }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell><strong>Başlık</strong></TableCell>
              <TableCell><strong>Açıklama</strong></TableCell>
              <TableCell><strong>Resim</strong></TableCell>
              <TableCell><strong>İşlemler</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading && stories.length === 0 ? (
              <TableRow>
                <TableCell colSpan={4} align="center">
                  Yükleniyor...
                </TableCell>
              </TableRow>
            ) : stories.length === 0 ? (
              <TableRow>
                <TableCell colSpan={4} align="center">
                  Henüz hikaye bulunmuyor
                </TableCell>
              </TableRow>
            ) : (
              stories.map((story) => (
                <TableRow key={story.id}>
                  <TableCell>{story.title}</TableCell>
                  <TableCell>
                    {story.description ? story.description.substring(0, 100) + (story.description.length > 100 ? '...' : '') : '-'}
                  </TableCell>
                  <TableCell>
                    {story.imageUrl ? (
                      <img 
                        src={story.imageUrl} 
                        alt={story.title} 
                        style={{ width: 50, height: 50, borderRadius: 4, objectFit: 'cover' }}
                        onError={(e) => {
                          (e.target as HTMLImageElement).src = 'https://via.placeholder.com/50x50?text=?';
                        }}
                      />
                    ) : (
                      <Box sx={{ width: 50, height: 50, bgcolor: '#f0f0f0', borderRadius: 1 }} />
                    )}
                  </TableCell>
                  <TableCell>
                    <IconButton 
                      onClick={() => handleDeleteStory(story.id)} 
                      color="error"
                      disabled={loading}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default StoryManagement; 