/**
 * SimpleNote PWA — Core Application Logic
 * Zero-friction personal expense journal.
 * Modern SVG Icons & Natural Gesture Sheet Dismiss.
 */

// MARK: - Modern SVG Icon Library (45 Curated Clean Outline 1.8px Icons)
const SVG_ICONS = {
  utensils: '<path d="M18 2v6a3 3 0 0 1-3 3 3 3 0 0 1-3-3V2"/><path d="M15 11v11"/><path d="M5 2v4a3 3 0 0 0 3 3v13"/><path d="M9 2v4"/>',
  coffee: '<path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/>',
  pizza: '<path d="M15 11h.01"/><path d="M11 15h.01"/><path d="M16 16h.01"/><path d="M2 16l20 6-6-20A20 20 0 0 0 2 16z"/>',
  cake: '<path d="M20 21v-8a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v8"/><path d="M4 16s2-1 4-1 4 1 4 1 2-1 4-1 4 1 4 1"/><line x1="2" y1="21" x2="22" y2="21"/><line x1="12" y1="7" x2="12" y2="11"/><circle cx="12" cy="4" r="1"/>',
  beer: '<path d="M17 11h1a3 3 0 0 1 0 6h-1"/><path d="M5 6h12v12a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V6z"/><line x1="9" y1="10" x2="9" y2="17"/><line x1="13" y1="10" x2="13" y2="17"/>',
  apple: '<path d="M12 20.94c1.5 0 2.75 1.06 4 1.06 3 0 6-8 6-12.22A4.91 4.91 0 0 0 17 5c-2.22 0-4 1.44-5 2-1-.56-2.78-2-5-2a4.9 4.9 0 0 0-5 4.78C2 14 5 22 8 22c1.25 0 2.5-1.06 4-1.06z"/><path d="M10 2c1 .5 2 2 2 5"/>',
  car: '<path d="M5 17h14m-12 0a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm14 0a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm-15-5l1.6-4.8A2 2 0 0 1 5.5 6h13a2 2 0 0 1 1.9 1.2L22 12v5H2v-5z"/>',
  bike: '<circle cx="5.5" cy="17.5" r="3.5"/><circle cx="18.5" cy="17.5" r="3.5"/><path d="M15 6a1 1 0 1 0 0-2 1 1 0 0 0 0 2zm-3 11.5L9 11l3-4h3l3 4.5"/><path d="M5.5 17.5L9 11"/>',
  bus: '<rect x="4" y="3" width="16" height="16" rx="2"/><path d="M4 11h16"/><path d="M8 15h.01"/><path d="M16 15h.01"/><path d="M6 19v2"/><path d="M18 19v2"/>',
  train: '<rect x="4" y="3" width="16" height="15" rx="2"/><path d="M4 11h16"/><circle cx="8" cy="15" r="1"/><circle cx="16" cy="15" r="1"/><path d="M8 18l-3 3"/><path d="M16 18l3 3"/>',
  fuel: '<line x1="3" y1="22" x2="15" y2="22"/><rect x="4" y="4" width="10" height="18" rx="1"/><path d="M14 9h2a2 2 0 0 1 2 2v7a2 2 0 0 0 2 2h0a2 2 0 0 0 2-2V9l-3-3"/><line x1="7" y1="8" x2="11" y2="8"/>',
  plane: '<path d="M17.8 19.2L16 11l3.5-3.5C21 6 21.5 4 21 3.5c-.5-.5-2.5 0-4 1.5L13.5 8.5 5.3 6.7c-.9-.2-1.6.4-1.4 1.3l1.8 5.7-3.2 3.2 1.4 1.4 4.3-1.4 4.3 4.3 1.4-1.4-1.4-4.3 5.7 1.8c.9.2 1.5-.5 1.3-1.4z"/>',
  'map-pin': '<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>',
  'shopping-bag': '<path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/>',
  'shopping-cart': '<circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>',
  tag: '<path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/>',
  package: '<line x1="16.5" y1="9.4" x2="7.5" y2="4.21"/><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/>',
  shirt: '<path d="M20.38 3.46L16 2a4 4 0 0 1-8 0L3.62 3.46a2 2 0 0 0-1.34 2.23l.58 3.47a1 1 0 0 0 .99.84H6v10c0 1.1.9 2 2 2h8a2 2 0 0 0 2-2V10h2.15a1 1 0 0 0 .99-.84l.58-3.47a2 2 0 0 0-1.34-2.23z"/>',
  receipt: '<path d="M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2l-2 1-2-1-2 1-2-1-2 1-2-1-2 1z"/><line x1="8" y1="7" x2="16" y2="7"/><line x1="8" y1="11" x2="16" y2="11"/><line x1="8" y1="15" x2="12" y2="15"/>',
  'credit-card': '<rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/>',
  wallet: '<path d="M20 7H4a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2z"/><path d="M16 3H4a2 2 0 0 0-2 2v2"/><circle cx="16" cy="14" r="1"/>',
  briefcase: '<rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>',
  zap: '<polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>',
  droplet: '<path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"/>',
  wifi: '<path d="M5 12.55a11 11 0 0 1 14.08 0"/><path d="M1.42 9a16 16 0 0 1 21.16 0"/><path d="M8.53 16.11a6 6 0 0 1 6.95 0"/><line x1="12" y1="20" x2="12.01" y2="20"/>',
  phone: '<rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/>',
  ticket: '<rect x="2" y="6" width="20" height="12" rx="2"/><line x1="12" y1="6" x2="12" y2="18" stroke-dasharray="2 2"/>',
  gamepad: '<line x1="6" y1="12" x2="10" y2="12"/><line x1="8" y1="10" x2="8" y2="14"/><line x1="15" y1="13" x2="15.01" y2="13"/><line x1="18" y1="11" x2="18.01" y2="11"/><rect x="2" y="6" width="20" height="12" rx="6"/>',
  tv: '<rect x="2" y="7" width="20" height="15" rx="2" ry="2"/><polyline points="17 2 12 7 7 2"/>',
  music: '<path d="M9 18V5l12-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/>',
  camera: '<path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/>',
  pill: '<path d="M10.5 20.5l10-10a4.95 4.95 0 1 0-7-7l-10 10a4.95 4.95 0 1 0 7 7z"/><line x1="8.5" y1="8.5" x2="15.5" y2="15.5"/>',
  'heart-pulse': '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/><polyline points="3 12 8 12 10 9 14 15 16 12 21 12"/>',
  dumbbell: '<path d="M6.5 6.5l11 11"/><path d="M21 21l-1-1a3 3 0 0 0-4.24 0l-1.42 1.42a3 3 0 0 0 0 4.24l1 1a3 3 0 0 0 4.24 0l1.42-1.42a3 3 0 0 0 0-4.24z"/><path d="M3 3l1 1a3 3 0 0 0 4.24 0L9.66 2.58a3 3 0 0 0 0-4.24l-1-1a3 3 0 0 0-4.24 0L3 0a3 3 0 0 0 0 4.24z"/>',
  smile: '<circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2"/><line x1="9" y1="9" x2="9.01" y2="9"/><line x1="15" y1="9" x2="15.01" y2="9"/>',
  book: '<path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>',
  'graduation-cap': '<path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/>',
  users: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>',
  paw: '<circle cx="11" cy="4" r="2"/><circle cx="18" cy="8" r="2"/><circle cx="20" cy="16" r="2"/><path d="M9 10a5 5 0 0 1 5 5v3.5a3.5 3.5 0 0 1-6.84 1.05A4 4 0 0 1 4 16c0-3.3 2.7-6 5-6z"/>',
  home: '<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>',
  wrench: '<path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>',
  flame: '<path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/>',
  heart: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>',
  gift: '<polyline points="20 12 20 22 4 22 4 12"/><rect x="2" y="7" width="20" height="5"/><line x1="12" y1="22" x2="12" y2="7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/>',
  sparkles: '<path d="M12 2l2.4 7.2L22 12l-7.6 2.8L12 22l-2.4-7.2L2 12l7.6-2.8z"/>'
};

const EMOJI_TO_ICON_MAP = {
  '🍜': 'utensils',
  '🍔': 'utensils',
  '🍕': 'pizza',
  '🍰': 'cake',
  '🍺': 'beer',
  '🍎': 'apple',
  '🚗': 'car',
  '🚕': 'car',
  '🚲': 'bike',
  '🏍️': 'bike',
  '🚌': 'bus',
  '🚆': 'train',
  '⛽': 'fuel',
  '🛍️': 'shopping-bag',
  '🛒': 'shopping-cart',
  '🏷️': 'tag',
  '📦': 'package',
  '👕': 'shirt',
  '🧾': 'receipt',
  '⚡️': 'zap',
  '💧': 'droplet',
  '🌐': 'wifi',
  '💳': 'credit-card',
  '👛': 'wallet',
  '💼': 'briefcase',
  '🎬': 'ticket',
  '🎮': 'gamepad',
  '📺': 'tv',
  '☕': 'coffee',
  '💊': 'pill',
  '🩺': 'heart-pulse',
  '📚': 'book',
  '🎓': 'graduation-cap',
  '🤲': 'heart',
  '❤️': 'heart',
  '🎁': 'gift',
  '💡': 'sparkles',
  '✨': 'sparkles',
  '✈️': 'plane',
  '🏋️': 'dumbbell',
  '💻': 'phone',
  '📱': 'phone',
  '🐾': 'paw',
  '🏠': 'home',
  '🔧': 'wrench',
  '🔥': 'flame',
  '🎨': 'sparkles',
  '🌿': 'apple'
};

function renderSvgIcon(iconName, size = 18) {
  const content = SVG_ICONS[iconName] || SVG_ICONS['sparkles'];
  return `<svg viewBox="0 0 24 24" width="${size}" height="${size}" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${content}</svg>`;
}

function resolveCategoryIconName(catOrExp) {
  if (!catOrExp) return 'sparkles';

  // 1. Check direct categoryIcon property (used on expense)
  if (catOrExp.categoryIcon && SVG_ICONS[catOrExp.categoryIcon]) {
    return catOrExp.categoryIcon;
  }

  // 2. Check direct icon property (used on category object)
  if (catOrExp.icon && SVG_ICONS[catOrExp.icon]) {
    return catOrExp.icon;
  }

  // 3. If it's an expense with a categoryId, look up category in state.categories
  if (catOrExp.categoryId && typeof state !== 'undefined' && state.categories) {
    const found = state.categories.find((c) => c.id === catOrExp.categoryId);
    if (found && found.icon && SVG_ICONS[found.icon]) {
      return found.icon;
    }
    if (found && found.emoji && EMOJI_TO_ICON_MAP[found.emoji]) {
      return EMOJI_TO_ICON_MAP[found.emoji];
    }
  }

  // 4. Fallback match by category name
  const catName = catOrExp.name || catOrExp.categoryName;
  if (catName && typeof state !== 'undefined' && state.categories) {
    const found = state.categories.find((c) => c.name && c.name.toLowerCase() === catName.toLowerCase());
    if (found && found.icon && SVG_ICONS[found.icon]) {
      return found.icon;
    }
  }

  // 5. Fallback legacy emoji mapping
  const emoji = catOrExp.emoji || catOrExp.categoryEmoji;
  if (emoji && EMOJI_TO_ICON_MAP[emoji]) {
    return EMOJI_TO_ICON_MAP[emoji];
  }

  return 'sparkles';
}

function getCategoryIcon(catOrExp, size = 18) {
  const iconName = resolveCategoryIconName(catOrExp);
  return renderSvgIcon(iconName, size);
}

// MARK: - Default Constants
const DEFAULT_CATEGORIES = [
  { id: 'cat_food', name: 'Makan & Minum', icon: 'utensils', isDefault: true },
  { id: 'cat_trans', name: 'Transportasi', icon: 'car', isDefault: true },
  { id: 'cat_shop', name: 'Belanja', icon: 'shopping-bag', isDefault: true },
  { id: 'cat_bill', name: 'Tagihan', icon: 'receipt', isDefault: true },
  { id: 'cat_fun', name: 'Hiburan', icon: 'ticket', isDefault: true },
  { id: 'cat_coffee', name: 'Kopi & Jajan', icon: 'coffee', isDefault: true },
  { id: 'cat_health', name: 'Kesehatan', icon: 'pill', isDefault: true },
  { id: 'cat_edu', name: 'Pendidikan', icon: 'book', isDefault: true },
  { id: 'cat_charity', name: 'Donasi', icon: 'heart', isDefault: true },
  { id: 'cat_other', name: 'Lainnya', icon: 'sparkles', isDefault: true }
];

const WARM_PALETTE = [
  '#C1440E', // Terracotta primary
  '#D97706', // Amber gold
  '#EA580C', // Burnt sienna
  '#0F766E', // Deep teal
  '#4D7C0F', // Warm olive
  '#BE185D', // Dusty rose
  '#475569', // Slate ink
  '#B45309', // Warm ochre
  '#E11D48', // Crimson rose
  '#78716C'  // Warm stone
];

// MARK: - State Management
const state = {
  currentTab: 'tab-today',
  expenses: [],
  categories: [],
  
  // History Filter State
  historyMonth: new Date(),
  selectedHistoryDate: null,
  historySearchQuery: '',
  historyCategoryFilter: null,
  
  // Stats State
  statsMonth: new Date(),
  statsTrendMode: 'weekly',
  statsSelectedWeekId: null,
  statsSelectedDay: null,
  shouldAutoScrollDaily: false,
  
  // Add Expense Modal State
  addAmountString: '0',
  addCategory: null,
  addDate: new Date(),
  
  // Edit Expense Modal State
  editingExpenseId: null,
  editAmountString: '0',
  editCategory: null,
  editDate: new Date(),
  editCustomIcon: 'sparkles'
};

// MARK: - Helpers
function formatRupiah(num) {
  const rounded = Math.round(num);
  return 'Rp ' + rounded.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

function formatCompactRupiah(num) {
  if (num >= 1000000) {
    const val = (num / 1000000).toFixed(1).replace('.0', '');
    return val + 'jt';
  } else if (num >= 1000) {
    const val = (num / 1000).toFixed(0);
    return val + 'rb';
  }
  return formatRupiah(num);
}

function formatTime(dateObj) {
  const d = new Date(dateObj);
  const h = String(d.getHours()).padStart(2, '0');
  const m = String(d.getMinutes()).padStart(2, '0');
  return `${h}:${m}`;
}

function formatEditedTime(dateObj) {
  if (!dateObj) return '';
  const d = new Date(dateObj);
  const now = new Date();
  const isSameDay = d.getFullYear() === now.getFullYear() &&
                    d.getMonth() === now.getMonth() &&
                    d.getDate() === now.getDate();
  const timeStr = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
  if (isSameDay) return timeStr;
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agu', 'Sep', 'Okt', 'Nov', 'Des'];
  return `${d.getDate()} ${months[d.getMonth()]} ${timeStr}`;
}

function formatRelativeDate(dateObj) {
  const d = new Date(dateObj);
  const now = new Date();
  
  const isSameDay = d.getFullYear() === now.getFullYear() &&
                    d.getMonth() === now.getMonth() &&
                    d.getDate() === now.getDate();
  if (isSameDay) return 'Hari Ini';
  
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  const isYesterday = d.getFullYear() === yesterday.getFullYear() &&
                      d.getMonth() === yesterday.getMonth() &&
                      d.getDate() === yesterday.getDate();
  if (isYesterday) return 'Kemarin';

  const days = ['Minggu', 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agu', 'Sep', 'Okt', 'Nov', 'Des'];
  return `${days[d.getDay()]}, ${d.getDate()} ${months[d.getMonth()]} ${d.getFullYear()}`;
}

// MARK: - Bottom Floating Toast Card (High Visibility & Swipeable)
let toastTimeout = null;
function showToast({ type = 'success', title, message, duration = 4000 }) {
  triggerHaptic();
  const container = document.getElementById('toast-container');
  if (!container) return;

  container.innerHTML = '';
  if (toastTimeout) {
    clearTimeout(toastTimeout);
    toastTimeout = null;
  }

  const iconSvgMap = {
    success: '<svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>',
    info: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>',
    danger: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>',
    error: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>'
  };

  const card = document.createElement('div');
  card.className = 'bottom-toast-card';
  card.innerHTML = `
    <span class="toast-icon-badge ${type}">${iconSvgMap[type] || iconSvgMap.success}</span>
    <div class="toast-content">
      <span class="toast-title">${escapeHtml(title)}</span>
      ${message ? `<span class="toast-message">${escapeHtml(message)}</span>` : ''}
    </div>
    <button type="button" class="toast-close-btn" title="Tutup">✕</button>
  `;

  const dismiss = () => {
    if (toastTimeout) {
      clearTimeout(toastTimeout);
      toastTimeout = null;
    }
    card.classList.remove('visible');
    card.classList.add('hiding');
    setTimeout(() => {
      if (card.parentNode) card.parentNode.removeChild(card);
    }, 280);
  };

  // Close button tap
  const closeBtn = card.querySelector('.toast-close-btn');
  if (closeBtn) {
    closeBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      triggerHaptic();
      dismiss();
    });
  }

  // Swipe-down to dismiss gesture
  let startY = 0;
  let currentDelta = 0;
  card.addEventListener('touchstart', (e) => {
    if (e.touches && e.touches[0]) {
      startY = e.touches[0].clientY;
      currentDelta = 0;
    }
  }, { passive: true });

  card.addEventListener('touchmove', (e) => {
    if (e.touches && e.touches[0]) {
      const delta = e.touches[0].clientY - startY;
      if (delta > 0) {
        currentDelta = delta;
        card.style.transform = `translate(-50%, ${delta}px)`;
      }
    }
  }, { passive: true });

  card.addEventListener('touchend', () => {
    if (currentDelta > 35) {
      triggerHaptic();
      dismiss();
    } else {
      card.style.transform = '';
    }
  });

  container.appendChild(card);

  requestAnimationFrame(() => {
    card.classList.add('visible');
  });

  toastTimeout = setTimeout(() => {
    dismiss();
  }, duration);
}

function formatMonthYear(dateObj) {
  const d = new Date(dateObj);
  const months = ['Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni', 'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember'];
  return `${months[d.getMonth()]} ${d.getFullYear()}`;
}

function triggerHaptic() {
  if (navigator.vibrate) {
    navigator.vibrate(12);
  }
}

// MARK: - IndexedDB Storage Engine (Rock-Solid for iOS WebKit Standalone)
const DB_NAME = 'simplenote_db';
const DB_VERSION = 1;
let dbInstance = null;

function openDB() {
  return new Promise((resolve) => {
    if (dbInstance) return resolve(dbInstance);
    if (!window.indexedDB) {
      console.warn('IndexedDB not supported, falling back to localStorage');
      return resolve(null);
    }

    const request = indexedDB.open(DB_NAME, DB_VERSION);
    request.onupgradeneeded = (e) => {
      const db = e.target.result;
      if (!db.objectStoreNames.contains('expenses')) {
        db.createObjectStore('expenses', { keyPath: 'id' });
      }
      if (!db.objectStoreNames.contains('categories')) {
        db.createObjectStore('categories', { keyPath: 'id' });
      }
    };
    request.onsuccess = (e) => {
      dbInstance = e.target.result;
      resolve(dbInstance);
    };
    request.onerror = (e) => {
      console.error('IndexedDB open error:', e);
      resolve(null);
    };
  });
}

function idbGetAll(storeName) {
  return new Promise((resolve) => {
    openDB().then((db) => {
      if (!db) return resolve(null);
      try {
        const tx = db.transaction(storeName, 'readonly');
        const store = tx.objectStore(storeName);
        const req = store.getAll();
        req.onsuccess = () => resolve(req.result || []);
        req.onerror = () => resolve(null);
      } catch (err) {
        console.error('idbGetAll error:', err);
        resolve(null);
      }
    });
  });
}

function idbSaveAll(storeName, items) {
  return new Promise((resolve) => {
    openDB().then((db) => {
      if (!db) return resolve(false);
      try {
        const tx = db.transaction(storeName, 'readwrite');
        const store = tx.objectStore(storeName);
        store.clear();
        items.forEach((item) => store.put(item));
        tx.oncomplete = () => resolve(true);
        tx.onerror = () => resolve(false);
      } catch (err) {
        console.error('idbSaveAll error:', err);
        resolve(false);
      }
    });
  });
}

// MARK: - Server Sync Engine (Solves iOS Safari vs Home Screen PWA Sandbox Barrier)
let isSyncing = false;

async function syncWithServer() {
  if (isSyncing) return;
  isSyncing = true;

  try {
    const res = await fetch('/api/data', { cache: 'no-store' });
    if (res.ok) {
      const serverData = await res.json();
      const serverExpenses = Array.isArray(serverData.expenses) ? serverData.expenses : [];
      const serverCategories = Array.isArray(serverData.categories) && serverData.categories.length > 0
        ? serverData.categories
        : [];

      // Fast signature check to avoid useless re-renders when data is unchanged
      const serverSig = JSON.stringify(serverExpenses) + '|' + JSON.stringify(serverCategories);
      const localSig = JSON.stringify(state.expenses) + '|' + JSON.stringify(state.categories);

      if (serverSig === localSig) {
        updateSyncIndicator(true);
        return;
      }

      // Reconcile: If server is empty but local has data, upload local data to server
      if (serverExpenses.length === 0 && state.expenses.length > 0) {
        await pushDataToServer();
      } else if (serverExpenses.length > 0) {
        // Merge expenses: union by id, preserving server's authoritative version
        const localMap = new Map(state.expenses.map(e => [e.id, e]));
        serverExpenses.forEach(se => {
          localMap.set(se.id, se);
        });
        state.expenses = Array.from(localMap.values()).sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

        if (serverCategories.length > 0) {
          state.categories = serverCategories;
        }

        sanitizeData();

        // Save merged data locally
        idbSaveAll('categories', state.categories);
        idbSaveAll('expenses', state.expenses);
        try {
          localStorage.setItem('simplenote_categories', JSON.stringify(state.categories));
          localStorage.setItem('simplenote_expenses', JSON.stringify(state.expenses));
        } catch (e) {}

        renderAll();
      }

      updateSyncIndicator(true);
    }
  } catch (err) {
    console.log('Server sync offline or unreachable:', err);
    updateSyncIndicator(false);
  } finally {
    isSyncing = false;
  }
}

async function pushDataToServer() {
  try {
    const payload = {
      expenses: state.expenses,
      categories: state.categories,
      lastUpdated: new Date().toISOString()
    };
    const res = await fetch('/api/data', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      updateSyncIndicator(true);
    }
  } catch (e) {
    console.log('Error pushing data to server:', e);
    updateSyncIndicator(false);
  }
}

function updateSyncIndicator(isOnline) {
  const badge = document.getElementById('storage-status-badge');
  if (badge) {
    if (isOnline) {
      badge.innerHTML = '<span class="accent-dot" style="background:#10B981;"></span> Cloud Tersinkron (24/7)';
    } else {
      badge.innerHTML = '<span class="accent-dot" style="background:#F59E0B;"></span> Lokal Offline (IndexedDB)';
    }
  }
}

// MARK: - Data Sanitization & Self-Healing
function sanitizeData() {
  if (Array.isArray(state.categories)) {
    state.categories.forEach((cat) => {
      if (!cat.icon || !SVG_ICONS[cat.icon]) {
        const def = DEFAULT_CATEGORIES.find((dc) => dc.id === cat.id);
        if (def && def.icon) {
          cat.icon = def.icon;
        } else if (cat.emoji && EMOJI_TO_ICON_MAP[cat.emoji]) {
          cat.icon = EMOJI_TO_ICON_MAP[cat.emoji];
        } else {
          cat.icon = 'sparkles';
        }
      }
      delete cat.emoji;
    });
  }

  if (Array.isArray(state.expenses)) {
    state.expenses.forEach((exp) => {
      exp.categoryIcon = resolveCategoryIconName(exp);
      delete exp.categoryEmoji;
      delete exp.emoji;
    });
  }
}

// MARK: - Persistence
async function loadData() {
  // Request storage persistence from iOS WebKit
  if (navigator.storage && navigator.storage.persist) {
    try {
      const isPersisted = await navigator.storage.persist();
      console.log('Storage persisted status:', isPersisted);
    } catch (e) {
      console.log('Error requesting persistence:', e);
    }
  }

  // 1. Try loading from IndexedDB first
  const dbExpenses = await idbGetAll('expenses');
  const dbCategories = await idbGetAll('categories');

  if (dbCategories && dbCategories.length > 0) {
    state.categories = dbCategories;
  } else {
    // Check localStorage fallback
    const storedCats = localStorage.getItem('simplenote_categories');
    state.categories = storedCats ? JSON.parse(storedCats) : DEFAULT_CATEGORIES;
    idbSaveAll('categories', state.categories);
  }

  if (dbExpenses && dbExpenses.length > 0) {
    state.expenses = dbExpenses;
  } else {
    // Check localStorage fallback
    const storedExpenses = localStorage.getItem('simplenote_expenses');
    state.expenses = storedExpenses ? JSON.parse(storedExpenses) : [];
    if (state.expenses.length > 0) {
      idbSaveAll('expenses', state.expenses);
    }
  }

  sanitizeData();

  // Dual redundancy: sync to localStorage
  try {
    localStorage.setItem('simplenote_categories', JSON.stringify(state.categories));
    localStorage.setItem('simplenote_expenses', JSON.stringify(state.expenses));
  } catch (e) {}

  renderAll();

  // Immediately sync with server
  await syncWithServer();
}

function saveData() {
  // Dual write: IndexedDB + localStorage (immediate UI responsiveness)
  idbSaveAll('categories', state.categories);
  idbSaveAll('expenses', state.expenses);

  try {
    localStorage.setItem('simplenote_categories', JSON.stringify(state.categories));
    localStorage.setItem('simplenote_expenses', JSON.stringify(state.expenses));
  } catch (e) {
    console.error('LocalStorage write error:', e);
  }

  // Background push to server
  pushDataToServer();
}

// MARK: - Backup Export & Import (Safety Net)
function exportBackupJSON() {
  const backupPayload = {
    app: 'SimpleNote',
    version: '1.3.0',
    exportDate: new Date().toISOString(),
    totalExpenses: state.expenses.length,
    categories: state.categories,
    expenses: state.expenses
  };

  const jsonStr = JSON.stringify(backupPayload, null, 2);
  const blob = new Blob([jsonStr], { type: 'application/json' });
  const filename = `simplenote_backup_${new Date().toISOString().slice(0, 10)}.json`;

  if (navigator.canShare && navigator.canShare({ files: [new File([blob], filename, { type: 'application/json' })] })) {
    const file = new File([blob], filename, { type: 'application/json' });
    navigator.share({
      title: 'Cadangan SimpleNote',
      text: 'File cadangan pengeluaran SimpleNote',
      files: [file]
    }).catch(() => {
      downloadBlobFallback(blob, filename);
    });
  } else {
    downloadBlobFallback(blob, filename);
  }

  showToast({
    type: 'success',
    title: 'Cadangan Diekspor',
    message: 'File cadangan JSON berhasil disiapkan'
  });
}

function downloadBlobFallback(blob, filename) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

function importBackupJSON(file) {
  const reader = new FileReader();
  reader.onload = (e) => {
    try {
      const parsed = JSON.parse(e.target.result);
      if (Array.isArray(parsed.expenses)) {
        state.expenses = parsed.expenses;
        if (Array.isArray(parsed.categories) && parsed.categories.length > 0) {
          state.categories = parsed.categories;
        }
        sanitizeData();
        saveData();
        renderAll();
        showToast({
          type: 'success',
          title: 'Cadangan Dipulihkan',
          message: `${state.expenses.length} transaksi berhasil dimuat`
        });
      } else {
        showToast({
          type: 'error',
          title: 'Format Tidak Valid',
          message: 'File bukan cadangan SimpleNote'
        });
      }
    } catch (err) {
      showToast({
        type: 'error',
        title: 'Gagal Membaca File',
        message: err.message
      });
    }
  };
  reader.readAsText(file);
}

// ==========================================================================
// NATURAL GESTURE: SWIPE-DOWN TO DISMISS SHEETS
// ==========================================================================
function attachSheetSwipeToDismiss(modalBackdrop, sheetElement, onDismiss) {
  if (!modalBackdrop || !sheetElement) return;

  let startY = 0;
  let currentY = 0;
  let isDragging = false;
  let startTime = 0;

  const header = sheetElement.querySelector('.sheet-header') || sheetElement;
  const grabber = sheetElement.querySelector('.sheet-grabber') || sheetElement;

  function onTouchStart(e) {
    if (sheetElement.scrollTop > 0) return;
    const touch = e.touches[0];
    startY = touch.clientY;
    currentY = startY;
    startTime = Date.now();
    isDragging = true;
    sheetElement.style.transition = 'none';
  }

  function onTouchMove(e) {
    if (!isDragging) return;
    const touch = e.touches[0];
    currentY = touch.clientY;
    const deltaY = currentY - startY;

    if (deltaY > 0) {
      if (e.cancelable) e.preventDefault();
      sheetElement.style.transform = `translateY(${deltaY}px)`;
      const opacity = Math.max(0.1, 1 - deltaY / 300);
      modalBackdrop.style.backgroundColor = `rgba(0, 0, 0, ${0.4 * opacity})`;
    } else {
      sheetElement.style.transform = 'translateY(0)';
    }
  }

  function onTouchEnd() {
    if (!isDragging) return;
    isDragging = false;
    const deltaY = currentY - startY;
    const elapsed = Date.now() - startTime;
    const velocity = deltaY / Math.max(1, elapsed);

    sheetElement.style.transition = 'transform 0.22s cubic-bezier(0.16, 1, 0.3, 1)';
    modalBackdrop.style.transition = 'background-color 0.22s ease';

    // Drag past 80px or fast downward flick
    if (deltaY > 80 || (deltaY > 30 && velocity > 0.35)) {
      triggerHaptic();
      sheetElement.style.transform = 'translateY(100%)';
      modalBackdrop.style.backgroundColor = 'rgba(0, 0, 0, 0)';
      setTimeout(() => {
        sheetElement.style.transform = '';
        modalBackdrop.style.backgroundColor = '';
        sheetElement.style.transition = '';
        modalBackdrop.style.transition = '';
        onDismiss();
      }, 220);
    } else {
      sheetElement.style.transform = 'translateY(0)';
      modalBackdrop.style.backgroundColor = 'rgba(0, 0, 0, 0.4)';
      setTimeout(() => {
        sheetElement.style.transform = '';
        sheetElement.style.transition = '';
        modalBackdrop.style.backgroundColor = '';
      }, 220);
    }
  }

  // Attach touch listeners to grabber & header
  grabber.addEventListener('touchstart', onTouchStart, { passive: true });
  grabber.addEventListener('touchmove', onTouchMove, { passive: false });
  grabber.addEventListener('touchend', onTouchEnd);

  header.addEventListener('touchstart', onTouchStart, { passive: true });
  header.addEventListener('touchmove', onTouchMove, { passive: false });
  header.addEventListener('touchend', onTouchEnd);
}

// MARK: - DOM Initialization
document.addEventListener('DOMContentLoaded', async () => {
  await loadData();
  setupNavigation();
  setupTodayTab();
  setupHistoryTab();
  setupStatsTab();
  setupSettingsTab();
  setupAddExpenseModal();
  setupEditExpenseModal();
  setupCategoryManagerModal();
  setupConfirmModal();
  setupAuthModal();

  renderAll();

  // Instant server sync
  syncWithServer();

  // Setup natural swipe-down to dismiss on all 3 sheets
  attachSheetSwipeToDismiss(
    document.getElementById('modal-add-expense'),
    document.querySelector('#modal-add-expense .modal-sheet'),
    () => { document.getElementById('modal-add-expense').classList.remove('open'); }
  );

  attachSheetSwipeToDismiss(
    document.getElementById('modal-edit-expense'),
    document.querySelector('#modal-edit-expense .modal-sheet'),
    () => { document.getElementById('modal-edit-expense').classList.remove('open'); }
  );

  attachSheetSwipeToDismiss(
    document.getElementById('modal-cat-manager'),
    document.querySelector('#modal-cat-manager .modal-sheet'),
    () => { document.getElementById('modal-cat-manager').classList.remove('open'); }
  );

  attachSheetSwipeToDismiss(
    document.getElementById('modal-confirm-dialog'),
    document.querySelector('#modal-confirm-dialog .modal-sheet'),
    () => { document.getElementById('modal-confirm-dialog').classList.remove('open'); }
  );

  attachSheetSwipeToDismiss(
    document.getElementById('modal-delete-password'),
    document.querySelector('#modal-delete-password .modal-sheet'),
    () => { document.getElementById('modal-delete-password').classList.remove('open'); }
  );

  // Re-sync when app becomes visible or reconnected
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') {
      syncWithServer();
    }
  });

  window.addEventListener('online', syncWithServer);

  // Periodic poll every 5s to keep Safari and Home Screen PWA synchronized
  setInterval(syncWithServer, 5000);

  // Register service worker
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('sw.js?v=10').catch((err) => {
      console.log('SW registration note:', err);
    });
  }
});

function renderAll() {
  renderTodayView();
  renderHistoryView();
  renderStatsView();
  renderSettingsView();
}

// MARK: - Navigation
function setupNavigation() {
  const navBtns = document.querySelectorAll('.bottom-nav .nav-item');
  const screenTitle = document.getElementById('screen-title');

  const titles = {
    'tab-today': 'Hari Ini',
    'tab-history': 'Riwayat',
    'tab-stats': 'Statistik',
    'tab-settings': 'Pengaturan'
  };

  navBtns.forEach((btn) => {
    btn.addEventListener('click', () => {
      triggerHaptic();
      const targetTab = btn.getAttribute('data-tab');
      state.currentTab = targetTab;

      navBtns.forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');

      document.querySelectorAll('.tab-pane').forEach((p) => p.classList.remove('active'));
      const activePane = document.getElementById(targetTab);
      if (activePane) activePane.classList.add('active');

      screenTitle.textContent = titles[targetTab] || 'SimpleNote';

      if (targetTab === 'tab-today') renderTodayView();
      if (targetTab === 'tab-history') renderHistoryView();
      if (targetTab === 'tab-stats') {
        state.shouldAutoScrollDaily = true;
        renderStatsView();
      }
      if (targetTab === 'tab-settings') renderSettingsView();
    });
  });
}

// ==========================================================================
// TAB 1: TODAY VIEW
// ==========================================================================
function setupTodayTab() {
  const btnOpenAdd = document.getElementById('btn-open-add');
  btnOpenAdd.addEventListener('click', () => {
    triggerHaptic();
    openAddModal();
  });
}

function renderTodayView() {
  const now = new Date();
  const days = ['MINGGU', 'SENIN', 'SELASA', 'RABU', 'KAMIS', 'JUMAT', 'SABTU'];
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agu', 'Sep', 'Okt', 'Nov', 'Des'];
  
  document.getElementById('hero-day-name').textContent = days[now.getDay()];
  document.getElementById('hero-full-date').textContent = `${now.getDate()} ${months[now.getMonth()]} ${now.getFullYear()}`;

  // Filter today's expenses
  const todayExpenses = state.expenses.filter((exp) => {
    const d = new Date(exp.timestamp);
    return d.getFullYear() === now.getFullYear() &&
           d.getMonth() === now.getMonth() &&
           d.getDate() === now.getDate();
  });

  const todaySum = todayExpenses.reduce((acc, curr) => acc + curr.amount, 0);
  document.getElementById('today-total-display').textContent = formatRupiah(todaySum);

  const statusText = document.getElementById('today-status-text');
  if (todayExpenses.length === 0) {
    statusText.textContent = 'Dompetmu masih tenang hari ini';
  } else {
    statusText.textContent = `${todayExpenses.length} transaksi tercatat secara sadar`;
  }

  const countBadge = document.getElementById('today-count-badge');
  countBadge.textContent = todayExpenses.length > 0 ? `${todayExpenses.length} transaksi` : '';

  const listContainer = document.getElementById('today-expenses-list');
  listContainer.innerHTML = '';

  if (todayExpenses.length === 0) {
    listContainer.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">${renderSvgIcon('coffee', 28)}</div>
        <div class="empty-title">Belum Ada Catatan Hari Ini</div>
        <div class="empty-msg">Setiap pengeluaran kecil itu berharga. Ketuk tombol <strong>Catat</strong> di bawah untuk memulai jurnal belanjamu hari ini.</div>
      </div>
    `;
  } else {
    todayExpenses.forEach((exp) => {
      const row = createExpenseRowElement(exp);
      listContainer.appendChild(row);
    });
  }
}

function createExpenseRowElement(exp) {
  const row = document.createElement('div');
  row.className = 'expense-row';
  const editedHtml = exp.updatedAt ? `<div class="expense-edited">Diedit: ${formatEditedTime(exp.updatedAt)}</div>` : '';
  row.innerHTML = `
    <span class="expense-svg-icon">${getCategoryIcon(exp, 18)}</span>
    <div class="expense-detail">
      <div class="expense-name">${escapeHtml(exp.categoryName || 'Lainnya')}</div>
      ${exp.note ? `<div class="expense-note">${escapeHtml(exp.note)}</div>` : ''}
    </div>
    <div class="expense-right">
      <div class="expense-amount">${formatRupiah(exp.amount)}</div>
      <div class="expense-time">${formatTime(exp.timestamp)}</div>
      ${editedHtml}
    </div>
  `;

  row.addEventListener('click', () => {
    triggerHaptic();
    openEditModal(exp);
  });

  return row;
}

function escapeHtml(str) {
  return String(str).replace(/[&<>"']/g, (m) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  })[m]);
}

// ==========================================================================
// TAB 2: HISTORY VIEW (Search, Calendar & Chips)
// ==========================================================================
function setupHistoryTab() {
  const searchInput = document.getElementById('history-search-input');
  const clearBtn = document.getElementById('btn-clear-search');

  searchInput.addEventListener('input', (e) => {
    state.historySearchQuery = e.target.value;
    clearBtn.style.display = state.historySearchQuery ? 'block' : 'none';
    renderHistoryView();
  });

  clearBtn.addEventListener('click', () => {
    searchInput.value = '';
    state.historySearchQuery = '';
    clearBtn.style.display = 'none';
    renderHistoryView();
  });

  document.getElementById('btn-prev-month').addEventListener('click', () => {
    triggerHaptic();
    state.historyMonth.setMonth(state.historyMonth.getMonth() - 1);
    state.selectedHistoryDate = null;
    renderHistoryView();
  });

  document.getElementById('btn-next-month').addEventListener('click', () => {
    triggerHaptic();
    state.historyMonth.setMonth(state.historyMonth.getMonth() + 1);
    state.selectedHistoryDate = null;
    renderHistoryView();
  });
}

function renderHistoryView() {
  const calWrapper = document.getElementById('calendar-wrapper');
  const searchActive = state.historySearchQuery.trim().length > 0;
  calWrapper.style.display = searchActive ? 'none' : 'block';

  document.getElementById('calendar-month-label').textContent = formatMonthYear(state.historyMonth);

  // Render Calendar Grid
  renderCalendarDaysGrid();

  // Render Category Filter Chips
  renderHistoryCategoryChips();

  // Filter Expenses
  let filtered = [...state.expenses];

  if (searchActive) {
    const q = state.historySearchQuery.toLowerCase();
    filtered = filtered.filter((exp) => {
      const matchNote = (exp.note || '').toLowerCase().includes(q);
      const matchCat = (exp.categoryName || '').toLowerCase().includes(q);
      const matchAmt = formatRupiah(exp.amount).toLowerCase().includes(q);
      return matchNote || matchCat || matchAmt;
    });
  } else {
    // Filter by active month
    filtered = filtered.filter((exp) => {
      const d = new Date(exp.timestamp);
      return d.getFullYear() === state.historyMonth.getFullYear() &&
             d.getMonth() === state.historyMonth.getMonth();
    });

    // Filter by selected date if any
    if (state.selectedHistoryDate) {
      filtered = filtered.filter((exp) => {
        const d = new Date(exp.timestamp);
        return d.getFullYear() === state.selectedHistoryDate.getFullYear() &&
               d.getMonth() === state.selectedHistoryDate.getMonth() &&
               d.getDate() === state.selectedHistoryDate.getDate();
      });
    }
  }

  // Filter by category chip if selected
  if (state.historyCategoryFilter) {
    filtered = filtered.filter((exp) => {
      return exp.categoryId === state.historyCategoryFilter.id ||
             exp.categoryName === state.historyCategoryFilter.name;
    });
  }

  // Calculate Total
  const total = filtered.reduce((acc, curr) => acc + curr.amount, 0);
  document.getElementById('history-filtered-total').textContent = `Total: ${formatRupiah(total)}`;

  const statusLabel = document.getElementById('history-filter-status');
  if (searchActive) {
    statusLabel.textContent = `Pencarian: "${state.historySearchQuery}"`;
  } else if (state.selectedHistoryDate) {
    statusLabel.textContent = formatRelativeDate(state.selectedHistoryDate);
  } else {
    statusLabel.textContent = 'Riwayat Bulan Ini';
  }

  // Render Grouped List
  const listContainer = document.getElementById('history-grouped-list');
  listContainer.innerHTML = '';

  if (filtered.length === 0) {
    listContainer.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">${renderSvgIcon('sparkles', 28)}</div>
        <div class="empty-title">Tidak Ada Catatan</div>
        <div class="empty-msg">Tidak ada transaksi yang cocok dengan kriteria filter saat ini.</div>
      </div>
    `;
    return;
  }

  // Group by day
  const groups = {};
  filtered.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

  filtered.forEach((exp) => {
    const d = new Date(exp.timestamp);
    const dateKey = `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
    if (!groups[dateKey]) groups[dateKey] = [];
    groups[dateKey].push(exp);
  });

  Object.keys(groups).forEach((dateKey) => {
    const items = groups[dateKey];
    const firstDate = new Date(items[0].timestamp);
    const dayTotal = items.reduce((acc, c) => acc + c.amount, 0);

    const groupEl = document.createElement('div');
    groupEl.className = 'history-group';

    groupEl.innerHTML = `
      <div class="history-group-header">
        <span>${formatRelativeDate(firstDate)}</span>
        <span class="group-day-total">${formatRupiah(dayTotal)}</span>
      </div>
      <div class="expense-list"></div>
    `;

    const subList = groupEl.querySelector('.expense-list');
    items.forEach((exp) => {
      subList.appendChild(createExpenseRowElement(exp));
    });

    listContainer.appendChild(groupEl);
  });
}

function renderCalendarDaysGrid() {
  const grid = document.getElementById('calendar-days-grid');
  grid.innerHTML = '';

  const y = state.historyMonth.getFullYear();
  const m = state.historyMonth.getMonth();

  const firstDayIndex = new Date(y, m, 1).getDay();
  const totalDays = new Date(y, m + 1, 0).getDate();

  // Find which days have expenses
  const activeDays = new Set();
  state.expenses.forEach((exp) => {
    const d = new Date(exp.timestamp);
    if (d.getFullYear() === y && d.getMonth() === m) {
      activeDays.add(d.getDate());
    }
  });

  const today = new Date();

  // Blank slots
  for (let i = 0; i < firstDayIndex; i++) {
    const blank = document.createElement('div');
    grid.appendChild(blank);
  }

  for (let day = 1; day <= totalDays; day++) {
    const cell = document.createElement('div');
    cell.className = 'day-cell';
    cell.textContent = day;

    const isToday = today.getFullYear() === y && today.getMonth() === m && today.getDate() === day;
    if (isToday) cell.classList.add('today');

    if (state.selectedHistoryDate &&
        state.selectedHistoryDate.getFullYear() === y &&
        state.selectedHistoryDate.getMonth() === m &&
        state.selectedHistoryDate.getDate() === day) {
      cell.classList.add('selected');
    }

    if (activeDays.has(day)) {
      const dot = document.createElement('span');
      dot.className = 'day-dot';
      cell.appendChild(dot);
    }

    cell.addEventListener('click', () => {
      triggerHaptic();
      if (state.selectedHistoryDate && state.selectedHistoryDate.getDate() === day) {
        state.selectedHistoryDate = null; // deselect
      } else {
        state.selectedHistoryDate = new Date(y, m, day);
      }
      renderHistoryView();
    });

    grid.appendChild(cell);
  }
}

function renderHistoryCategoryChips() {
  const container = document.getElementById('history-category-filters');
  container.innerHTML = '';

  // "Semua" Chip
  const allBtn = document.createElement('button');
  allBtn.className = `chip-btn ${state.historyCategoryFilter === null ? 'active' : ''}`;
  allBtn.textContent = 'Semua';
  allBtn.addEventListener('click', () => {
    triggerHaptic();
    state.historyCategoryFilter = null;
    renderHistoryView();
  });
  container.appendChild(allBtn);

  // Category Chips
  state.categories.forEach((cat) => {
    const chip = document.createElement('button');
    const isSelected = state.historyCategoryFilter && state.historyCategoryFilter.id === cat.id;
    chip.className = `chip-btn ${isSelected ? 'active' : ''}`;
    chip.innerHTML = `<span class="chip-svg-icon">${getCategoryIcon(cat, 15)}</span> <span>${escapeHtml(cat.name)}</span>`;
    chip.addEventListener('click', () => {
      triggerHaptic();
      state.historyCategoryFilter = isSelected ? null : cat;
      renderHistoryView();
    });
    container.appendChild(chip);
  });
}

// ==========================================================================
// TAB 3: STATS VIEW
// ==========================================================================
function setupStatsTab() {
  document.getElementById('btn-stats-prev').addEventListener('click', () => {
    triggerHaptic();
    state.statsMonth.setMonth(state.statsMonth.getMonth() - 1);
    state.statsSelectedWeekId = null;
    state.statsSelectedDay = null;
    state.shouldAutoScrollDaily = true;
    renderStatsView();
  });

  document.getElementById('btn-stats-next').addEventListener('click', () => {
    triggerHaptic();
    state.statsMonth.setMonth(state.statsMonth.getMonth() + 1);
    state.statsSelectedWeekId = null;
    state.statsSelectedDay = null;
    state.shouldAutoScrollDaily = true;
    renderStatsView();
  });

  const btnWeekly = document.getElementById('btn-trend-weekly');
  const btnDaily = document.getElementById('btn-trend-daily');

  if (btnWeekly && btnDaily) {
    btnWeekly.addEventListener('click', () => {
      triggerHaptic();
      state.statsTrendMode = 'weekly';
      btnWeekly.classList.add('active');
      btnDaily.classList.remove('active');
      renderStatsTrendChart();
    });

    btnDaily.addEventListener('click', () => {
      triggerHaptic();
      state.statsTrendMode = 'daily';
      state.shouldAutoScrollDaily = true;
      btnDaily.classList.add('active');
      btnWeekly.classList.remove('active');
      renderStatsTrendChart();
    });
  }
}

function renderStatsView() {
  document.getElementById('stats-month-label').textContent = formatMonthYear(state.statsMonth);

  const y = state.statsMonth.getFullYear();
  const m = state.statsMonth.getMonth();

  const monthlyExpenses = state.expenses.filter((exp) => {
    const d = new Date(exp.timestamp);
    return d.getFullYear() === y && d.getMonth() === m;
  });

  const total = monthlyExpenses.reduce((acc, curr) => acc + curr.amount, 0);
  document.getElementById('stats-monthly-total').textContent = formatRupiah(total);
  document.getElementById('donut-center-amount').textContent = formatCompactRupiah(total);

  const daysInMonth = new Date(y, m + 1, 0).getDate();
  const avg = total > 0 ? total / daysInMonth : 0;
  document.getElementById('stats-average-daily').textContent = `Rata-rata: ${formatCompactRupiah(avg)}/hari`;

  // Aggregate Category Breakdown
  const catMap = {};
  monthlyExpenses.forEach((exp) => {
    const name = exp.categoryName || 'Lainnya';
    const icon = exp.categoryIcon || exp.icon || (exp.categoryEmoji ? EMOJI_TO_ICON_MAP[exp.categoryEmoji] : 'sparkles');
    if (!catMap[name]) catMap[name] = { amount: 0, icon, name };
    catMap[name].amount += exp.amount;
  });

  const catStats = Object.values(catMap).sort((a, b) => b.amount - a.amount);

  // Render SVG Donut Chart
  renderDonutChart(catStats, total);

  // Render Category Breakdown List
  const breakdownList = document.getElementById('stats-category-list');
  breakdownList.innerHTML = '';

  if (catStats.length === 0) {
    breakdownList.innerHTML = `<div class="empty-msg" style="text-align:center; padding: 20px 0;">Belum ada pengeluaran di bulan ini.</div>`;
  } else {
    catStats.forEach((cat, idx) => {
      const color = WARM_PALETTE[idx % WARM_PALETTE.length];
      const pct = total > 0 ? ((cat.amount / total) * 100).toFixed(1) : '0';

      const row = document.createElement('div');
      row.className = 'cat-breakdown-row';
      row.innerHTML = `
        <span class="cat-color-dot" style="background:${color}"></span>
        <span class="chip-svg-icon" style="margin-right:6px; color:${color};">${getCategoryIcon(cat, 16)}</span>
        <span class="cat-label">${escapeHtml(cat.name)}</span>
        <span class="cat-percent">${pct}%</span>
        <span class="cat-sum">${formatRupiah(cat.amount)}</span>
      `;
      breakdownList.appendChild(row);
    });
  }

  // Render Interactive Weekly / Daily Bars
  renderStatsTrendChart();
}

function renderDonutChart(catStats, total) {
  const svg = document.getElementById('donut-svg');
  svg.innerHTML = '';

  const cx = 100, cy = 100, r = 70;
  const strokeWidth = 24;
  const circumference = 2 * Math.PI * r;

  // Background ring
  const bgCircle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
  bgCircle.setAttribute('cx', cx);
  bgCircle.setAttribute('cy', cy);
  bgCircle.setAttribute('r', r);
  bgCircle.setAttribute('fill', 'none');
  bgCircle.setAttribute('stroke', '#E8E0D8');
  bgCircle.setAttribute('stroke-width', strokeWidth);
  bgCircle.setAttribute('opacity', '0.4');
  svg.appendChild(bgCircle);

  if (total <= 0 || catStats.length === 0) return;

  let currentOffset = 0;

  catStats.forEach((cat, idx) => {
    const color = WARM_PALETTE[idx % WARM_PALETTE.length];
    const portion = cat.amount / total;
    const strokeDash = portion * circumference;

    const segment = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
    segment.setAttribute('cx', cx);
    segment.setAttribute('cy', cy);
    segment.setAttribute('r', r);
    segment.setAttribute('fill', 'none');
    segment.setAttribute('stroke', color);
    segment.setAttribute('stroke-width', strokeWidth);
    segment.setAttribute('stroke-dasharray', `${strokeDash} ${circumference - strokeDash}`);
    segment.setAttribute('stroke-dashoffset', -currentOffset);
    segment.setAttribute('transform', `rotate(-90 ${cx} ${cy})`);

    svg.appendChild(segment);
    currentOffset += strokeDash;
  });
}

function renderTrendBreakdown(matchingExpenses, title) {
  const container = document.getElementById('trend-breakdown-container');
  const titleEl = document.getElementById('trend-breakdown-title');
  const badgeEl = document.getElementById('trend-breakdown-badge');
  const listEl = document.getElementById('trend-breakdown-list');

  if (!container || !listEl) return;

  container.style.display = 'block';
  if (titleEl) titleEl.textContent = title;
  if (badgeEl) badgeEl.textContent = `${matchingExpenses.length} Transaksi`;

  listEl.innerHTML = '';
  if (matchingExpenses.length === 0) {
    listEl.innerHTML = `<div class="trend-breakdown-empty">Tidak ada catatan pengeluaran pada periode ini.</div>`;
    return;
  }

  // Sort by timestamp descending
  const sorted = [...matchingExpenses].sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
  sorted.forEach((exp) => {
    const row = createExpenseRowElement(exp);
    listEl.appendChild(row);
  });
}

function renderStatsTrendChart() {
  const container = document.getElementById('daily-bar-chart');
  const tooltipDate = document.getElementById('trend-tooltip-date');
  const tooltipAmount = document.getElementById('trend-tooltip-amount');
  if (!container) return;

  container.innerHTML = '';
  const isWeekly = state.statsTrendMode === 'weekly';
  container.className = `bars-container ${isWeekly ? 'mode-weekly' : 'mode-daily'}`;

  const y = state.statsMonth.getFullYear();
  const m = state.statsMonth.getMonth();
  const daysInMonth = new Date(y, m + 1, 0).getDate();

  const monthlyExpenses = state.expenses.filter((exp) => {
    const d = new Date(exp.timestamp);
    return d.getFullYear() === y && d.getMonth() === m;
  });

  const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agu', 'Sep', 'Okt', 'Nov', 'Des'];
  const dayNamesShort = ['Min', 'Sen', 'Sel', 'Rab', 'Kam', 'Jum', 'Sab'];
  const monthName = monthNames[m];

  const today = new Date();
  const isCurrentMonth = today.getFullYear() === y && today.getMonth() === m;
  const currentDay = today.getDate();

  if (isWeekly) {
    // Generate Weeks: W1 (1-7), W2 (8-14), W3 (15-21), W4 (22-28), W5 (29-end if > 28)
    const weeks = [
      { id: 'W1', label: 'W1', start: 1, end: 7, sum: 0, count: 0 },
      { id: 'W2', label: 'W2', start: 8, end: 14, sum: 0, count: 0 },
      { id: 'W3', label: 'W3', start: 15, end: 21, sum: 0, count: 0 },
      { id: 'W4', label: 'W4', start: 22, end: 28, sum: 0, count: 0 }
    ];
    if (daysInMonth > 28) {
      weeks.push({ id: 'W5', label: 'W5', start: 29, end: daysInMonth, sum: 0, count: 0 });
    }

    monthlyExpenses.forEach((exp) => {
      const day = new Date(exp.timestamp).getDate();
      for (const w of weeks) {
        if (day >= w.start && day <= w.end) {
          w.sum += exp.amount;
          w.count += 1;
          break;
        }
      }
    });

    const maxVal = Math.max(...weeks.map((w) => w.sum), 1);

    // Determine active week
    let activeWeek = null;
    if (state.statsSelectedWeekId) {
      activeWeek = weeks.find((w) => w.id === state.statsSelectedWeekId);
    }
    if (!activeWeek) {
      const defaultIndex = isCurrentMonth
        ? weeks.findIndex((w) => currentDay >= w.start && currentDay <= w.end)
        : (weeks.findIndex((w) => w.sum > 0) !== -1 ? weeks.findIndex((w) => w.sum > 0) : 0);
      activeWeek = weeks[defaultIndex !== -1 ? defaultIndex : 0] || weeks[0];
      state.statsSelectedWeekId = activeWeek.id;
    }

    weeks.forEach((w) => {
      const isSelected = w.id === state.statsSelectedWeekId;
      const pct = w.sum > 0 ? Math.max((w.sum / maxVal) * 100, 6) : 3;
      const col = document.createElement('div');
      col.className = `bar-col ${isSelected ? 'selected' : ''}`;

      const microAmount = w.sum > 0 ? formatCompactRupiah(w.sum) : 'Rp 0';

      col.innerHTML = `
        <span class="bar-val-micro">${microAmount}</span>
        <div class="bar-track">
          <div class="bar-fill" style="height:${pct}%; ${w.sum > 0 ? '' : 'background:#E8E0D8; opacity:0.35;'}"></div>
        </div>
        <span class="bar-label">${w.label}</span>
        <span class="bar-sublabel">${w.start}-${w.end}</span>
      `;

      col.addEventListener('click', () => {
        triggerHaptic();
        state.statsSelectedWeekId = w.id;
        container.querySelectorAll('.bar-col').forEach((c) => c.classList.remove('selected'));
        col.classList.add('selected');
        const titleStr = `Minggu ${w.label} (${w.start} - ${w.end} ${monthName})`;
        if (tooltipDate) {
          tooltipDate.textContent = titleStr;
        }
        if (tooltipAmount) {
          tooltipAmount.textContent = `${formatRupiah(w.sum)} (${w.count} transaksi)`;
        }

        const matching = monthlyExpenses.filter((exp) => {
          const day = new Date(exp.timestamp).getDate();
          return day >= w.start && day <= w.end;
        });
        renderTrendBreakdown(matching, titleStr);
      });

      container.appendChild(col);
    });

    // Populate tooltip and breakdown for currently selected week
    if (activeWeek) {
      const titleStr = `Minggu ${activeWeek.label} (${activeWeek.start} - ${activeWeek.end} ${monthName})`;
      if (tooltipDate) tooltipDate.textContent = titleStr;
      if (tooltipAmount) tooltipAmount.textContent = `${formatRupiah(activeWeek.sum)} (${activeWeek.count} transaksi)`;
      const matching = monthlyExpenses.filter((exp) => {
        const day = new Date(exp.timestamp).getDate();
        return day >= activeWeek.start && day <= activeWeek.end;
      });
      renderTrendBreakdown(matching, titleStr);
    }
  } else {
    // Daily Mode: Days 1 to daysInMonth in horizontal scrollable container
    const dailyData = [];
    for (let d = 1; d <= daysInMonth; d++) {
      dailyData.push({ day: d, sum: 0, count: 0 });
    }

    monthlyExpenses.forEach((exp) => {
      const d = new Date(exp.timestamp).getDate();
      if (d >= 1 && d <= daysInMonth) {
        dailyData[d - 1].sum += exp.amount;
        dailyData[d - 1].count += 1;
      }
    });

    const maxVal = Math.max(...dailyData.map((d) => d.sum), 1);
    let todayElement = null;
    let selectedCol = null;

    // Determine active day
    let activeDay = state.statsSelectedDay;
    if (!activeDay || activeDay < 1 || activeDay > daysInMonth) {
      activeDay = isCurrentMonth ? currentDay : (dailyData.find((d) => d.sum > 0)?.day || 1);
      state.statsSelectedDay = activeDay;
    }

    dailyData.forEach((d) => {
      const isToday = isCurrentMonth && d.day === currentDay;
      const isSelected = d.day === activeDay;
      const dateObj = new Date(y, m, d.day);
      const dayName = dayNamesShort[dateObj.getDay()];

      const pct = d.sum > 0 ? Math.max((d.sum / maxVal) * 100, 6) : 3;
      const col = document.createElement('div');
      col.className = `bar-col ${isToday ? 'is-today' : ''} ${isSelected ? 'selected' : ''}`;

      const microAmount = d.sum > 0 ? formatCompactRupiah(d.sum) : '';

      col.innerHTML = `
        <span class="bar-val-micro">${microAmount}</span>
        <div class="bar-track">
          <div class="bar-fill" style="height:${pct}%; ${d.sum > 0 ? '' : 'background:#E8E0D8; opacity:0.35;'}"></div>
        </div>
        <span class="bar-label">${d.day}</span>
        <span class="bar-sublabel">${dayName}</span>
      `;

      if (isToday) todayElement = col;
      if (isSelected) selectedCol = col;

      col.addEventListener('click', () => {
        triggerHaptic();
        state.statsSelectedDay = d.day;
        container.querySelectorAll('.bar-col').forEach((c) => c.classList.remove('selected'));
        col.classList.add('selected');
        const titleStr = `${dayName}, ${d.day} ${monthName} ${y}${isToday ? ' (Hari Ini)' : ''}`;
        if (tooltipDate) {
          tooltipDate.textContent = titleStr;
        }
        if (tooltipAmount) {
          tooltipAmount.textContent = `${formatRupiah(d.sum)}${d.count > 0 ? ` (${d.count} transaksi)` : ' (Nihil)'}`;
        }

        const matching = monthlyExpenses.filter((exp) => {
          return new Date(exp.timestamp).getDate() === d.day;
        });
        renderTrendBreakdown(matching, titleStr);
      });

      container.appendChild(col);
    });

    // Populate tooltip & breakdown for active day immediately
    const dObj = dailyData[activeDay - 1];
    if (dObj) {
      const dateObj = new Date(y, m, dObj.day);
      const dayName = dayNamesShort[dateObj.getDay()];
      const isToday = isCurrentMonth && dObj.day === currentDay;
      const titleStr = `${dayName}, ${dObj.day} ${monthName} ${y}${isToday ? ' (Hari Ini)' : ''}`;
      if (tooltipDate) tooltipDate.textContent = titleStr;
      if (tooltipAmount) tooltipAmount.textContent = `${formatRupiah(dObj.sum)}${dObj.count > 0 ? ` (${dObj.count} transaksi)` : ' (Nihil)'}`;
      const matching = monthlyExpenses.filter((exp) => new Date(exp.timestamp).getDate() === dObj.day);
      renderTrendBreakdown(matching, titleStr);
    }

    // Auto-scroll ONLY when requested (e.g. user switched month, tab, or mode)
    if (state.shouldAutoScrollDaily) {
      state.shouldAutoScrollDaily = false;
      const scrollTarget = selectedCol || todayElement;
      if (scrollTarget) {
        setTimeout(() => {
          scrollTarget.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
        }, 100);
      }
    }
  }
}

// ==========================================================================
// SECURITY & PASSWORD HELPERS
// ==========================================================================
const KEY_DELETE_PASSWORD_HASH = 'sn_delete_password_hash';

async function sha256Hex(text) {
  const enc = new TextEncoder().encode(text);
  const buf = await crypto.subtle.digest('SHA-256', enc);
  return Array.from(new Uint8Array(buf))
    .map((b) => b.toString(16).padStart(2, '0'))
    .join('');
}

function hasDeletePassword() {
  return Boolean(localStorage.getItem(KEY_DELETE_PASSWORD_HASH));
}

function setDeletePasswordHash(hash) {
  localStorage.setItem(KEY_DELETE_PASSWORD_HASH, hash);
  updatePasswordRowVisibility();
}

async function verifyDeletePassword(plainText) {
  const stored = localStorage.getItem(KEY_DELETE_PASSWORD_HASH);
  if (!stored) return false;
  const hash = await sha256Hex(plainText);
  return stored === hash;
}

function updatePasswordRowVisibility() {
  const row = document.getElementById('row-change-password');
  const divider = document.getElementById('row-change-password-divider');
  const hasPass = hasDeletePassword();
  if (row) row.style.display = hasPass ? 'flex' : 'none';
  if (divider) divider.style.display = hasPass ? 'block' : 'none';
}

// ==========================================================================
// REUSABLE CONFIRMATION MODAL
// ==========================================================================
let activeConfirmCallback = null;

function showConfirmModal({ icon = '⚠️', title = 'Konfirmasi', message, confirmText = 'Lanjutkan', isDanger = false, onConfirm }) {
  const modal = document.getElementById('modal-confirm-dialog');
  if (!modal) {
    if (confirm(message)) {
      if (typeof onConfirm === 'function') onConfirm();
    }
    return;
  }
  document.getElementById('confirm-dialog-icon').textContent = icon;
  document.getElementById('confirm-dialog-title').textContent = title;
  document.getElementById('confirm-dialog-message').textContent = message;
  const proceedBtn = document.getElementById('btn-confirm-proceed');
  proceedBtn.textContent = confirmText;
  proceedBtn.classList.toggle('danger', Boolean(isDanger));
  activeConfirmCallback = onConfirm;
  modal.classList.add('open');
}

function setupConfirmModal() {
  const modal = document.getElementById('modal-confirm-dialog');
  if (!modal) return;
  const cancelBtn = document.getElementById('btn-confirm-cancel');
  const proceedBtn = document.getElementById('btn-confirm-proceed');

  cancelBtn.addEventListener('click', () => {
    modal.classList.remove('open');
    activeConfirmCallback = null;
  });

  proceedBtn.addEventListener('click', () => {
    modal.classList.remove('open');
    if (typeof activeConfirmCallback === 'function') {
      const cb = activeConfirmCallback;
      activeConfirmCallback = null;
      cb();
    }
  });

  modal.addEventListener('click', (e) => {
    if (e.target === modal) {
      modal.classList.remove('open');
      activeConfirmCallback = null;
    }
  });
}

// ==========================================================================
// AUTH MODAL FOR SENSITIVE ACTIONS
// ==========================================================================
let currentAuthMode = 'verify'; // 'setup' | 'verify' | 'change'
let onAuthSuccessCallback = null;

function openAuthModal(mode, onSuccess) {
  currentAuthMode = mode;
  onAuthSuccessCallback = onSuccess;
  const modal = document.getElementById('modal-delete-password');
  if (!modal) return;
  const title = document.getElementById('auth-modal-title');
  const desc = document.getElementById('auth-modal-desc');
  const oldField = document.getElementById('auth-field-old');
  const newField = document.getElementById('auth-field-new');
  const confirmField = document.getElementById('auth-field-confirm');
  const newLabel = document.getElementById('auth-new-label');
  const errorMsg = document.getElementById('auth-error-msg');
  const submitBtn = document.getElementById('btn-auth-submit');

  document.getElementById('input-auth-old').value = '';
  document.getElementById('input-auth-new').value = '';
  document.getElementById('input-auth-confirm').value = '';
  errorMsg.style.display = 'none';
  errorMsg.textContent = '';

  if (mode === 'setup') {
    title.textContent = 'Buat Sandi Rahasia';
    desc.textContent = 'Untuk melindungi data sensitif, tentukan sandi rahasia yang hanya kamu ketahui sebelum menghapus semua data.';
    oldField.style.display = 'none';
    newField.style.display = 'block';
    confirmField.style.display = 'block';
    newLabel.textContent = 'Sandi Rahasia Baru';
    submitBtn.textContent = 'Simpan & Lanjut';
    submitBtn.classList.remove('danger');
  } else if (mode === 'verify') {
    title.textContent = 'Autentikasi Sandi';
    desc.textContent = 'Masukkan sandi rahasia untuk memverifikasi penghapusan seluruh riwayat transaksi.';
    oldField.style.display = 'none';
    newField.style.display = 'block';
    confirmField.style.display = 'none';
    newLabel.textContent = 'Sandi Rahasia';
    submitBtn.textContent = 'Verifikasi';
    submitBtn.classList.add('danger');
  } else if (mode === 'change') {
    title.textContent = 'Ubah Sandi Rahasia';
    desc.textContent = 'Masukkan sandi lama dan tentukan sandi rahasia yang baru.';
    oldField.style.display = 'block';
    newField.style.display = 'block';
    confirmField.style.display = 'block';
    newLabel.textContent = 'Sandi Baru';
    submitBtn.textContent = 'Simpan Sandi Baru';
    submitBtn.classList.remove('danger');
  }

  modal.classList.add('open');
  setTimeout(() => {
    const focusTarget = mode === 'change' ? document.getElementById('input-auth-old') : document.getElementById('input-auth-new');
    if (focusTarget) focusTarget.focus();
  }, 100);
}

function setupAuthModal() {
  const modal = document.getElementById('modal-delete-password');
  if (!modal) return;
  const closeBtn = document.getElementById('btn-close-auth-modal');
  const submitBtn = document.getElementById('btn-auth-submit');
  const errorMsg = document.getElementById('auth-error-msg');

  closeBtn.addEventListener('click', () => {
    modal.classList.remove('open');
  });

  submitBtn.addEventListener('click', async () => {
    errorMsg.style.display = 'none';
    const oldVal = document.getElementById('input-auth-old').value;
    const newVal = document.getElementById('input-auth-new').value;
    const confirmVal = document.getElementById('input-auth-confirm').value;

    if (currentAuthMode === 'setup') {
      if (!newVal.trim()) {
        errorMsg.textContent = 'Sandi tidak boleh kosong';
        errorMsg.style.display = 'block';
        return;
      }
      if (newVal !== confirmVal) {
        errorMsg.textContent = 'Konfirmasi sandi tidak cocok';
        errorMsg.style.display = 'block';
        return;
      }
      const hash = await sha256Hex(newVal);
      setDeletePasswordHash(hash);
      modal.classList.remove('open');
      if (typeof onAuthSuccessCallback === 'function') onAuthSuccessCallback();
    } else if (currentAuthMode === 'verify') {
      const isValid = await verifyDeletePassword(newVal);
      if (isValid) {
        modal.classList.remove('open');
        if (typeof onAuthSuccessCallback === 'function') onAuthSuccessCallback();
      } else {
        errorMsg.textContent = 'Sandi salah! Periksa kembali.';
        errorMsg.style.display = 'block';
      }
    } else if (currentAuthMode === 'change') {
      const isOldValid = await verifyDeletePassword(oldVal);
      if (!isOldValid) {
        errorMsg.textContent = 'Sandi lama tidak sesuai';
        errorMsg.style.display = 'block';
        return;
      }
      if (!newVal.trim()) {
        errorMsg.textContent = 'Sandi baru tidak boleh kosong';
        errorMsg.style.display = 'block';
        return;
      }
      if (newVal !== confirmVal) {
        errorMsg.textContent = 'Konfirmasi sandi baru tidak cocok';
        errorMsg.style.display = 'block';
        return;
      }
      const newHash = await sha256Hex(newVal);
      setDeletePasswordHash(newHash);
      modal.classList.remove('open');
      showToast({
        type: 'success',
        title: 'Sandi Diperbarui',
        message: 'Sandi rahasia berhasil diubah'
      });
    }
  });

  modal.addEventListener('click', (e) => {
    if (e.target === modal) modal.classList.remove('open');
  });
}

// ==========================================================================
// TAB 4: SETTINGS VIEW
// ==========================================================================
function setupSettingsTab() {
  document.getElementById('btn-open-cat-manager').addEventListener('click', () => {
    triggerHaptic();
    openCategoryManager();
  });

  // Export JSON Backup
  document.getElementById('btn-export-backup').addEventListener('click', () => {
    triggerHaptic();
    exportBackupJSON();
  });

  // Import JSON Backup
  const importInput = document.getElementById('import-json-input');
  document.getElementById('btn-import-backup').addEventListener('click', () => {
    triggerHaptic();
    importInput.click();
  });

  importInput.addEventListener('change', (e) => {
    if (e.target.files && e.target.files[0]) {
      importBackupJSON(e.target.files[0]);
      e.target.value = '';
    }
  });

  // Ubah Sandi Rahasia
  const changePassRow = document.getElementById('row-change-password');
  if (changePassRow) {
    changePassRow.addEventListener('click', () => {
      triggerHaptic();
      openAuthModal('change');
    });
  }

  // Bersihkan Semua Data dengan Proteksi Sandi
  document.getElementById('btn-reset-data').addEventListener('click', () => {
    triggerHaptic();
    if (!hasDeletePassword()) {
      openAuthModal('setup', () => {
        showConfirmModal({
          icon: '⚠️',
          title: 'Hapus Semua Data?',
          message: 'Sandi tersimpan. Apakah kamu yakin ingin menghapus SELURUH catatan transaksi secara permanen?',
          confirmText: 'Hapus Permanen',
          isDanger: true,
          onConfirm: () => {
            state.expenses = [];
            saveData();
            renderAll();
            showToast({
              type: 'danger',
              title: 'Data Dibersihkan',
              message: 'Semua riwayat transaksi telah dihapus'
            });
          }
        });
      });
    } else {
      openAuthModal('verify', () => {
        showConfirmModal({
          icon: '⚠️',
          title: 'Hapus Semua Data?',
          message: 'Sandi terverifikasi. Apakah kamu yakin ingin menghapus SELURUH catatan transaksi secara permanen?',
          confirmText: 'Hapus Permanen',
          isDanger: true,
          onConfirm: () => {
            state.expenses = [];
            saveData();
            renderAll();
            showToast({
              type: 'danger',
              title: 'Data Dibersihkan',
              message: 'Semua riwayat transaksi telah dihapus'
            });
          }
        });
      });
    }
  });
}

function renderSettingsView() {
  document.getElementById('settings-total-records').textContent = `${state.expenses.length} transaksi`;
  updatePasswordRowVisibility();
}

// ==========================================================================
// ADD EXPENSE MODAL (< 5s fast flow)
// ==========================================================================
function setupAddExpenseModal() {
  const modal = document.getElementById('modal-add-expense');
  const cancelBtn = document.getElementById('btn-cancel-add');
  const saveBtn = document.getElementById('btn-save-expense');
  const dateInput = document.getElementById('add-date-input');
  const toggleDateBtn = document.getElementById('btn-toggle-date');

  cancelBtn.addEventListener('click', () => {
    modal.classList.remove('open');
  });

  toggleDateBtn.addEventListener('click', () => {
    dateInput.showPicker ? dateInput.showPicker() : dateInput.focus();
  });

  dateInput.addEventListener('change', (e) => {
    if (e.target.value) {
      state.addDate = new Date(e.target.value);
      document.getElementById('add-date-label').textContent = formatRelativeDate(state.addDate);
    }
  });

  // Numeric Keypad
  modal.querySelectorAll('.keypad-grid .key-btn').forEach((btn) => {
    btn.addEventListener('click', () => {
      triggerHaptic();
      const key = btn.getAttribute('data-key');
      handleKeypadInput('add', key);
    });
  });

  saveBtn.addEventListener('click', () => {
    saveNewExpense();
  });
}

function openAddModal() {
  state.addAmountString = '0';
  state.addDate = new Date();
  state.addCategory = state.categories[0] || DEFAULT_CATEGORIES[0];
  document.getElementById('add-note-input').value = '';
  document.getElementById('add-date-label').textContent = 'Hari ini';

  updateAddAmountDisplay();
  renderAddCategoryChips();

  document.getElementById('modal-add-expense').classList.add('open');
}

function handleKeypadInput(mode, key) {
  let str = mode === 'add' ? state.addAmountString : state.editAmountString;

  if (key === 'backspace') {
    if (str.length > 1) {
      str = str.slice(0, -1);
    } else {
      str = '0';
    }
  } else if (key === '000') {
    if (str !== '0' && str.length <= 7) {
      str += '000';
    }
  } else {
    // Digits 0-9
    if (str === '0') {
      str = key;
    } else if (str.length < 10) {
      str += key;
    }
  }

  if (mode === 'add') {
    state.addAmountString = str;
    updateAddAmountDisplay();
  } else {
    state.editAmountString = str;
    updateEditAmountDisplay();
  }
}

function updateAddAmountDisplay() {
  const amt = parseFloat(state.addAmountString) || 0;
  document.getElementById('add-amount-display').textContent = formatRupiah(amt);
  document.getElementById('btn-save-expense').disabled = amt <= 0;
}

function renderAddCategoryChips() {
  const container = document.getElementById('add-category-chips');
  container.innerHTML = '';

  state.categories.forEach((cat) => {
    const chip = document.createElement('button');
    const isSelected = state.addCategory && state.addCategory.id === cat.id;
    chip.className = `chip-btn ${isSelected ? 'active' : ''}`;
    chip.innerHTML = `<span class="chip-svg-icon">${getCategoryIcon(cat, 15)}</span> <span>${escapeHtml(cat.name)}</span>`;
    chip.addEventListener('click', () => {
      triggerHaptic();
      state.addCategory = cat;
      renderAddCategoryChips();
    });
    container.appendChild(chip);
  });
}

function saveNewExpense() {
  const amt = parseFloat(state.addAmountString) || 0;
  if (amt <= 0) return;

  const note = document.getElementById('add-note-input').value.trim();
  const cat = state.addCategory || state.categories[0];
  const icon = cat ? (cat.icon || resolveCategoryIconName(cat)) : 'sparkles';

  showConfirmModal({
    icon: '📝',
    title: 'Catat Pengeluaran?',
    message: `Catat pengeluaran sebesar ${formatRupiah(amt)} untuk kategori "${cat ? cat.name : 'Lainnya'}"?`,
    confirmText: 'Simpan',
    onConfirm: () => {
      const newExpense = {
        id: 'exp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 4),
        amount: amt,
        note: note,
        timestamp: state.addDate.toISOString(),
        categoryId: cat ? cat.id : 'cat_other',
        categoryName: cat ? cat.name : 'Lainnya',
        categoryIcon: icon
      };

      state.expenses.unshift(newExpense);
      saveData();

      document.getElementById('modal-add-expense').classList.remove('open');
      triggerHaptic();
      renderAll();

      showToast({
        type: 'success',
        title: 'Pengeluaran Dicatat',
        message: `${newExpense.note || newExpense.categoryName} • ${formatRupiah(newExpense.amount)}`
      });
    }
  });
}

// ==========================================================================
// EDIT EXPENSE MODAL
// ==========================================================================
function setupEditExpenseModal() {
  const modal = document.getElementById('modal-edit-expense');
  const cancelBtn = document.getElementById('btn-cancel-edit');
  const updateBtn = document.getElementById('btn-update-expense');
  const deleteBtn = document.getElementById('btn-delete-expense');
  const dateInput = document.getElementById('edit-date-input');
  const toggleDateBtn = document.getElementById('btn-edit-toggle-date');
  const selectIconBtn = document.getElementById('btn-edit-select-icon');
  const iconPickerGrid = document.getElementById('edit-icon-picker-grid');

  cancelBtn.addEventListener('click', () => {
    modal.classList.remove('open');
    if (iconPickerGrid) iconPickerGrid.style.display = 'none';
  });

  toggleDateBtn.addEventListener('click', () => {
    dateInput.showPicker ? dateInput.showPicker() : dateInput.focus();
  });

  dateInput.addEventListener('change', (e) => {
    if (e.target.value) {
      state.editDate = new Date(e.target.value);
      document.getElementById('edit-date-label').textContent = formatRelativeDate(state.editDate);
    }
  });

  // Populate Edit Icon Picker Grid with all 45 icons
  if (iconPickerGrid) {
    iconPickerGrid.innerHTML = '';
    const availableIcons = Object.keys(SVG_ICONS);
    availableIcons.forEach((iconName) => {
      const item = document.createElement('button');
      item.type = 'button';
      item.className = 'icon-picker-item';
      item.dataset.icon = iconName;
      item.innerHTML = renderSvgIcon(iconName, 20);
      item.addEventListener('click', () => {
        triggerHaptic();
        state.editCustomIcon = iconName;
        updateEditIconDisplay(true);
        iconPickerGrid.style.display = 'none';
      });
      iconPickerGrid.appendChild(item);
    });

    if (selectIconBtn) {
      selectIconBtn.addEventListener('click', () => {
        triggerHaptic();
        const isOpen = iconPickerGrid.style.display !== 'none';
        iconPickerGrid.style.display = isOpen ? 'none' : 'grid';
        if (!isOpen) {
          iconPickerGrid.querySelectorAll('.icon-picker-item').forEach((btn) => {
            btn.classList.toggle('selected', btn.dataset.icon === state.editCustomIcon);
          });
        }
      });
    }
  }

  modal.querySelectorAll('#edit-keypad .key-btn').forEach((btn) => {
    btn.addEventListener('click', () => {
      triggerHaptic();
      const key = btn.getAttribute('data-key');
      handleKeypadInput('edit', key);
    });
  });

  updateBtn.addEventListener('click', () => {
    saveEditedExpense();
  });

  deleteBtn.addEventListener('click', () => {
    triggerHaptic();
    showConfirmModal({
      icon: '🗑️',
      title: 'Hapus Catatan?',
      message: 'Apakah kamu yakin ingin menghapus catatan transaksi ini secara permanen?',
      confirmText: 'Hapus',
      isDanger: true,
      onConfirm: () => {
        state.expenses = state.expenses.filter((e) => e.id !== state.editingExpenseId);
        saveData();
        modal.classList.remove('open');
        renderAll();
        showToast({
          type: 'danger',
          title: 'Catatan Dihapus',
          message: 'Transaksi berhasil dihapus dari jurnal'
        });
      }
    });
  });
}

function updateEditIconDisplay(isCustom = false) {
  const preview = document.getElementById('edit-icon-preview');
  const status = document.getElementById('edit-icon-status');
  if (preview) {
    preview.innerHTML = renderSvgIcon(state.editCustomIcon || 'sparkles', 20);
  }
  if (status) {
    if (isCustom) {
      status.textContent = 'Icon Kustom';
      status.style.color = 'var(--sn-accent)';
    } else {
      status.textContent = state.editCategory ? `Kategori: ${state.editCategory.name}` : 'Sesuai Kategori';
      status.style.color = 'var(--sn-ink-sec)';
    }
  }
}

function openEditModal(exp) {
  state.editingExpenseId = exp.id;
  state.editAmountString = String(Math.round(exp.amount));
  state.editDate = new Date(exp.timestamp);
  
  // Find matching category
  state.editCategory = state.categories.find((c) => c.id === exp.categoryId) ||
                       state.categories.find((c) => c.name === exp.categoryName) ||
                       state.categories[0];

  // Resolve current icon from expense
  state.editCustomIcon = resolveCategoryIconName(exp);

  document.getElementById('edit-note-input').value = exp.note || '';
  document.getElementById('edit-date-label').textContent = formatRelativeDate(state.editDate);

  const infoEl = document.getElementById('edit-timestamp-info');
  if (infoEl) {
    if (exp.updatedAt) {
      infoEl.textContent = `Terakhir diedit: ${formatEditedTime(exp.updatedAt)}`;
      infoEl.style.display = 'block';
    } else {
      infoEl.style.display = 'none';
    }
  }

  const iconPickerGrid = document.getElementById('edit-icon-picker-grid');
  if (iconPickerGrid) iconPickerGrid.style.display = 'none';

  updateEditAmountDisplay();
  const catDefaultIcon = state.editCategory ? resolveCategoryIconName(state.editCategory) : '';
  const isCustom = Boolean(state.editCustomIcon && catDefaultIcon && state.editCustomIcon !== catDefaultIcon);
  updateEditIconDisplay(isCustom);
  renderEditCategoryChips();

  document.getElementById('modal-edit-expense').classList.add('open');
}

function updateEditAmountDisplay() {
  const amt = parseFloat(state.editAmountString) || 0;
  document.getElementById('edit-amount-display').textContent = formatRupiah(amt);
  document.getElementById('btn-update-expense').disabled = amt <= 0;
}

function renderEditCategoryChips() {
  const container = document.getElementById('edit-category-chips');
  container.innerHTML = '';

  state.categories.forEach((cat) => {
    const chip = document.createElement('button');
    const isSelected = state.editCategory && state.editCategory.id === cat.id;
    chip.className = `chip-btn ${isSelected ? 'active' : ''}`;
    chip.innerHTML = `<span class="chip-svg-icon">${getCategoryIcon(cat, 15)}</span> <span>${escapeHtml(cat.name)}</span>`;
    chip.addEventListener('click', () => {
      triggerHaptic();
      state.editCategory = cat;
      state.editCustomIcon = cat.icon || resolveCategoryIconName(cat);
      updateEditIconDisplay(false);
      renderEditCategoryChips();
      const grid = document.getElementById('edit-icon-picker-grid');
      if (grid) grid.style.display = 'none';
    });
    container.appendChild(chip);
  });
}

function saveEditedExpense() {
  const amt = parseFloat(state.editAmountString) || 0;
  if (amt <= 0) return;

  const note = document.getElementById('edit-note-input').value.trim();
  const cat = state.editCategory || state.categories[0];
  const finalIcon = state.editCustomIcon || (cat ? (cat.icon || resolveCategoryIconName(cat)) : 'sparkles');

  showConfirmModal({
    icon: '✏️',
    title: 'Simpan Perubahan?',
    message: 'Apakah kamu yakin ingin menyimpan perubahan pada transaksi ini?',
    confirmText: 'Simpan',
    onConfirm: () => {
      const idx = state.expenses.findIndex((e) => e.id === state.editingExpenseId);
      if (idx !== -1) {
        const existing = state.expenses[idx];
        state.expenses[idx] = {
          ...existing,
          amount: amt,
          note: note,
          timestamp: state.editDate.toISOString(),
          categoryId: cat ? cat.id : 'cat_other',
          categoryName: cat ? cat.name : 'Lainnya',
          categoryIcon: finalIcon,
          updatedAt: new Date().toISOString()
        };
        delete state.expenses[idx].categoryEmoji;
        delete state.expenses[idx].emoji;

        saveData();
      }

      document.getElementById('modal-edit-expense').classList.remove('open');
      const grid = document.getElementById('edit-icon-picker-grid');
      if (grid) grid.style.display = 'none';

      triggerHaptic();
      renderAll();

      showToast({
        type: 'info',
        title: 'Perubahan Disimpan',
        message: `${cat.name}${note ? ' • ' + note : ''} • ${formatRupiah(amt)}`
      });
    }
  });
}

// ==========================================================================
// CATEGORY MANAGER MODAL (With SVG Icon Picker)
// ==========================================================================
let selectedNewCatIcon = 'sparkles';

function setupCategoryManagerModal() {
  const modal = document.getElementById('modal-cat-manager');
  const preview = document.getElementById('selected-cat-icon-preview');
  const pickerGrid = document.getElementById('icon-picker-grid');
  const selectBtn = document.getElementById('btn-select-cat-icon');

  if (preview) preview.innerHTML = renderSvgIcon(selectedNewCatIcon, 20);

  // Populate icon picker grid with all 45 icons
  if (pickerGrid) {
    pickerGrid.innerHTML = '';
    const availableIcons = Object.keys(SVG_ICONS);
    availableIcons.forEach((iconName) => {
      const item = document.createElement('button');
      item.type = 'button';
      item.className = `icon-picker-item ${iconName === selectedNewCatIcon ? 'selected' : ''}`;
      item.dataset.icon = iconName;
      item.innerHTML = renderSvgIcon(iconName, 20);
      item.addEventListener('click', () => {
        triggerHaptic();
        selectedNewCatIcon = iconName;
        preview.innerHTML = renderSvgIcon(selectedNewCatIcon, 20);
        pickerGrid.style.display = 'none';
        pickerGrid.querySelectorAll('.icon-picker-item').forEach((b) => b.classList.remove('selected'));
        item.classList.add('selected');
      });
      pickerGrid.appendChild(item);
    });

    if (selectBtn) {
      selectBtn.addEventListener('click', () => {
        triggerHaptic();
        const isOpen = pickerGrid.style.display !== 'none';
        pickerGrid.style.display = isOpen ? 'none' : 'grid';
        if (!isOpen) {
          pickerGrid.querySelectorAll('.icon-picker-item').forEach((b) => {
            b.classList.toggle('selected', b.dataset.icon === selectedNewCatIcon);
          });
        }
      });
    }
  }

  document.getElementById('btn-close-cat-manager').addEventListener('click', () => {
    modal.classList.remove('open');
    if (pickerGrid) pickerGrid.style.display = 'none';
  });

  document.getElementById('btn-add-category').addEventListener('click', () => {
    triggerHaptic();
    const name = document.getElementById('new-cat-name').value.trim();
    if (!name) return;

    const newCat = {
      id: 'cat_custom_' + Date.now(),
      name: name,
      icon: selectedNewCatIcon,
      isDefault: false
    };

    state.categories.push(newCat);
    saveData();

    document.getElementById('new-cat-name').value = '';
    if (pickerGrid) pickerGrid.style.display = 'none';
    renderCategoryManagerList();
    renderAll();

    showToast({
      type: 'success',
      title: 'Kategori Ditambahkan',
      message: `Kategori "${name}" siap digunakan`
    });
  });
}

function openCategoryManager() {
  renderCategoryManagerList();
  document.getElementById('modal-cat-manager').classList.add('open');
}

function renderCategoryManagerList() {
  const list = document.getElementById('categories-manager-list');
  list.innerHTML = '';

  state.categories.forEach((cat) => {
    const row = document.createElement('div');
    row.className = 'manager-cat-row';
    row.innerHTML = `
      <span class="chip-svg-icon" style="width:24px; height:24px; margin-right:10px;">${getCategoryIcon(cat, 20)}</span>
      <span style="flex:1; font-weight:500;">${escapeHtml(cat.name)}</span>
      ${cat.isDefault ? `<span style="font-size:11px; color:#78716C;">Bawaan</span>` : `<button class="del-cat-btn">Hapus</button>`}
    `;

    if (!cat.isDefault) {
      row.querySelector('.del-cat-btn').addEventListener('click', () => {
        triggerHaptic();
        if (confirm(`Hapus kategori "${cat.name}"?`)) {
          state.categories = state.categories.filter((c) => c.id !== cat.id);
          saveData();
          renderCategoryManagerList();
          renderAll();

          showToast({
            type: 'danger',
            title: 'Kategori Dihapus',
            message: `Kategori "${cat.name}" telah dihapus`
          });
        }
      });
    }

    list.appendChild(row);
  });
}
