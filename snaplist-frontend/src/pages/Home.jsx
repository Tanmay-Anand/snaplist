import React, { useEffect, useState, useCallback } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import api from '../api/api'; // FIXED: Correct path
import { StickyNote, Bookmark, CheckSquare, TrendingUp } from 'lucide-react';

export default function Home() {
  //Accessing global auth state
  const user = useSelector(s => s.auth.user);
  const token = useSelector(s => s.auth.token); // Get token to check auth status
  //Local state for statistics
  const [stats, setStats] = useState({ notes: 0, bookmarks: 0, tasks: 0 });
  const [loading, setLoading] = useState(true);

  //fetching all stats at once
  const fetchStats = useCallback(async () => { //Without useCallback, React would think the function changed every render → causing infinite re-fetch loops.
    if (!token) {
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      
      // Fetch counts from each endpoint
      //you want all 3 statistics together. Instead of waiting for each one individually, 
      //Promise.all fires all three requests simultaneously.
      //This gives total count, even though we only requested size=1.
      //Smart and efficient.
      const [notesRes, bookmarksRes, tasksRes] = await Promise.all([
        api.get('/notes?page=0&size=1'),
        api.get('/bookmarks?page=0&size=1'),
        api.get('/tasks?page=0&size=1')
      ]);

      setStats({
        notes: notesRes.data.totalElements || 0,
        bookmarks: bookmarksRes.data.totalElements || 0,
        tasks: tasksRes.data.totalElements || 0
      });
    } catch (err) {
      console.error('Failed to fetch stats:', err);
    } finally {
      setLoading(false);
    }
  }, [token]); // Only depends on token

  // Fetch when the page loads or when auth changes
  //Therefore, you always have fresh stats.
  useEffect(() => {
    if (user && token) {
      fetchStats();
    }
  }, [user, token, fetchStats]);

  // Refetch when user returns to the tab/Home
  //You want the numbers to update immediately.
  useEffect(() => {
    const handleFocus = () => {
      if (user && token) {
        fetchStats();
      }
    };

    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, [user, token, fetchStats]);

  return (
    <div className="max-w-6xl mx-auto">
      <div className="mb-8">
        <h1 className="text-4xl font-bold mb-2">
          Welcome back, {user?.username}! 👋
        </h1>
        <p className="text-gray-600">
          Your personal workstation for TO-DO!
        </p>
      </div>

      {/* If API calls aren’t done yet → show a loading message. */}
      {loading ? (
        <div className="text-center py-12">Loading your workspace...</div>
      ) : (
        // Loaded → Show dashboard cards: Notes, Bookmarks, Tasks card.
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          
          {/* Tasks Card */}
          <Link to="/tasks" className="block">
            <div className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-lg transition cursor-pointer">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-green-100 rounded-lg">
                  <CheckSquare size={24} className="text-green-600" />
                </div>
                <span className="text-3xl font-bold text-green-600">{stats.tasks}</span>
              </div>
              <h3 className="text-lg font-semibold mb-1">Tasks</h3>
              <p className="text-gray-600 text-sm">
                Track your to-dos and stay productive
              </p>
            </div>
          </Link>
        </div>
      )}

    </div>
  );
}

