// Firebase configuration disabled for KVKK compliance
// Using PostgreSQL backend at https://kazanion.onrender.com/api instead

/* 
import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyCgw9bjma00NMMxWFMft582bIVN--F22QE",
  authDomain: "kazanion-admin.firebaseapp.com",
  projectId: "kazanion-admin",
  storageBucket: "kazanion-admin.firebasestorage.app",
  messagingSenderId: "194633260024",
  appId: "1:194633260024:web:05900f03d70b790582540d",
  measurementId: "G-M51XNY73S1"
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
export const auth = getAuth(app);
*/

// Dummy exports for TypeScript compatibility
export const db = null;
export const auth = null;

console.log("Firebase disabled - Using PostgreSQL API for KVKK compliance"); // Updated for PostgreSQL KVKK compliance
// Force update for KVKK PostgreSQL admin panel - 30 Haz 2025 Pts +03 16:17:29
