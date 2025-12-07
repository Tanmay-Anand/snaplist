// src/store/store.js
import { configureStore } from '@reduxjs/toolkit'; //Importing Redux Toolkit's main function
//Importing your slices
import authReducer from './slices/authSlice';
import tasksReducer from './slices/tasksSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    tasks: tasksReducer,
  },
});

export default store;
