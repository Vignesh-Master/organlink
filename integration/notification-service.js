/**
 * Notification Service for OrganLink
 * 
 * This service provides a wrapper around the WebSocket service
 * for handling notifications in the frontend.
 */

import webSocketService from './websocket-service';

class NotificationService {
  constructor() {
    this.subscriptionId = null;
    this.callbacks = [];
    this.connected = false;
    this.notifications = [];
    this.maxNotifications = 50; // Maximum number of notifications to store
  }

  /**
   * Initialize the notification service
   * @param {Function} callback - Callback function when a notification is received
   */
  initialize(callback) {
    if (callback) {
      this.addCallback(callback);
    }

    // Initialize WebSocket if not already connected
    if (!webSocketService.connected) {
      webSocketService.initialize(
        undefined,
        this.handleConnect.bind(this),
        this.handleError.bind(this)
      );
    } else {
      this.handleConnect();
    }

    return this;
  }

  /**
   * Handle WebSocket connection
   */
  handleConnect() {
    this.connected = true;
    
    // Subscribe to user-specific notifications
    this.subscriptionId = webSocketService.subscribe(
      '/user/queue/notifications',
      this.handleNotification.bind(this)
    );
    
    // Subscribe to broadcast notifications
    this.broadcastSubscriptionId = webSocketService.subscribe(
      '/topic/notifications',
      this.handleNotification.bind(this)
    );
    
    console.log('Notification service initialized');
  }

  /**
   * Handle WebSocket error
   * @param {Object} error - The error object
   */
  handleError(error) {
    console.error('Notification service error:', error);
    this.connected = false;
  }

  /**
   * Handle incoming notification
   * @param {Object} notification - The notification object
   */
  handleNotification(notification) {
    // Add notification to the list
    this.notifications.unshift(notification);
    
    // Limit the number of stored notifications
    if (this.notifications.length > this.maxNotifications) {
      this.notifications = this.notifications.slice(0, this.maxNotifications);
    }
    
    // Call all registered callbacks
    this.callbacks.forEach(callback => {
      try {
        callback(notification, this.notifications);
      } catch (error) {
        console.error('Error in notification callback:', error);
      }
    });
  }

  /**
   * Add a callback function for notifications
   * @param {Function} callback - The callback function
   */
  addCallback(callback) {
    if (typeof callback === 'function' && !this.callbacks.includes(callback)) {
      this.callbacks.push(callback);
    }
    return this;
  }

  /**
   * Remove a callback function
   * @param {Function} callback - The callback function to remove
   */
  removeCallback(callback) {
    this.callbacks = this.callbacks.filter(cb => cb !== callback);
    return this;
  }

  /**
   * Get all stored notifications
   * @returns {Array} The list of notifications
   */
  getNotifications() {
    return [...this.notifications];
  }

  /**
   * Clear all stored notifications
   */
  clearNotifications() {
    this.notifications = [];
    return this;
  }

  /**
   * Send a notification to a specific user
   * @param {string} userId - The user ID to send the notification to
   * @param {string} title - The notification title
   * @param {string} message - The notification message
   * @param {string} type - The notification type (INFO, WARNING, ERROR, SUCCESS)
   */
  sendToUser(userId, title, message, type = 'INFO') {
    if (!this.connected) {
      console.warn('Notification service not connected');
      return false;
    }
    
    webSocketService.send('/app/send', {
      userId,
      title,
      message,
      type,
      timestamp: new Date().toISOString()
    });
    
    return true;
  }

  /**
   * Broadcast a notification to all users
   * @param {string} title - The notification title
   * @param {string} message - The notification message
   * @param {string} type - The notification type (INFO, WARNING, ERROR, SUCCESS)
   */
  broadcast(title, message, type = 'INFO') {
    if (!this.connected) {
      console.warn('Notification service not connected');
      return false;
    }
    
    webSocketService.send('/app/broadcast', {
      title,
      message,
      type,
      timestamp: new Date().toISOString()
    });
    
    return true;
  }

  /**
   * Disconnect the notification service
   */
  disconnect() {
    if (this.subscriptionId) {
      webSocketService.unsubscribe(this.subscriptionId);
      this.subscriptionId = null;
    }
    
    if (this.broadcastSubscriptionId) {
      webSocketService.unsubscribe(this.broadcastSubscriptionId);
      this.broadcastSubscriptionId = null;
    }
    
    this.connected = false;
    return this;
  }
}

// Create a singleton instance
const notificationService = new NotificationService();

export default notificationService;