/**
 * WebSocket Service for OrganLink
 * 
 * This service provides real-time communication between the frontend and backend
 * for features like live organ matching notifications, status updates, and alerts.
 */

import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class WebSocketService {
  constructor() {
    this.client = null;
    this.subscriptions = new Map();
    this.connected = false;
    this.reconnectDelay = 5000;
    this.maxReconnectAttempts = 5;
    this.reconnectAttempts = 0;
  }

  /**
   * Initialize the WebSocket connection
   * @param {string} serverUrl - The WebSocket server URL
   * @param {Function} onConnect - Callback function when connection is established
   * @param {Function} onError - Callback function when connection error occurs
   */
  initialize(serverUrl = 'http://localhost:8081/api/v1/ws', onConnect = () => {}, onError = () => {}) {
    // Create a new STOMP client over SockJS
    this.client = new Client({
      webSocketFactory: () => new SockJS(serverUrl),
      debug: process.env.NODE_ENV === 'development' ? (msg) => console.log(msg) : () => {},
      reconnectDelay: this.reconnectDelay,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('WebSocket connection established');
        this.connected = true;
        this.reconnectAttempts = 0;
        onConnect();
      },
      onDisconnect: () => {
        console.log('WebSocket disconnected');
        this.connected = false;
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame);
        onError(frame);
      },
      onWebSocketClose: () => {
        console.log('WebSocket connection closed');
        this.connected = false;
        
        // Handle reconnection logic
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
          this.reconnectAttempts++;
          console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
        } else {
          console.error('Max reconnection attempts reached');
        }
      }
    });

    // Add JWT token to STOMP headers
    this.client.connectHeaders = this.getAuthHeaders();

    // Activate the client
    this.client.activate();
  }

  /**
   * Get authentication headers for WebSocket connection
   * @returns {Object} Headers object with authentication tokens
   */
  getAuthHeaders() {
    const headers = {};
    
    // Add appropriate token based on user type
    const hospitalToken = localStorage.getItem('hospital_token');
    const adminToken = localStorage.getItem('admin_token');
    const organizationToken = localStorage.getItem('organization_token');
    
    if (hospitalToken) {
      headers['Authorization'] = `Bearer ${hospitalToken}`;
      const tenantId = localStorage.getItem('hospital_tenant_id');
      if (tenantId) {
        headers['X-Tenant-ID'] = tenantId;
      }
    } else if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    } else if (organizationToken) {
      headers['Authorization'] = `Bearer ${organizationToken}`;
    }
    
    return headers;
  }

  /**
   * Subscribe to a topic
   * @param {string} topic - The topic to subscribe to
   * @param {Function} callback - Callback function when message is received
   * @returns {string} Subscription ID
   */
  subscribe(topic, callback) {
    if (!this.connected) {
      console.warn('WebSocket not connected. Will subscribe when connected.');
      return null;
    }
    
    const subscription = this.client.subscribe(topic, (message) => {
      try {
        const parsedBody = JSON.parse(message.body);
        callback(parsedBody, message);
      } catch (error) {
        console.error('Error parsing message:', error);
        callback(message.body, message);
      }
    });
    
    const subscriptionId = subscription.id;
    this.subscriptions.set(subscriptionId, { topic, callback });
    
    return subscriptionId;
  }

  /**
   * Unsubscribe from a topic
   * @param {string} subscriptionId - The subscription ID to unsubscribe
   */
  unsubscribe(subscriptionId) {
    if (!this.connected) {
      console.warn('WebSocket not connected');
      return;
    }
    
    if (this.subscriptions.has(subscriptionId)) {
      this.client.unsubscribe(subscriptionId);
      this.subscriptions.delete(subscriptionId);
    }
  }

  /**
   * Send a message to the server
   * @param {string} destination - The destination endpoint
   * @param {Object} body - The message body
   */
  send(destination, body) {
    if (!this.connected) {
      console.warn('WebSocket not connected. Cannot send message.');
      return;
    }
    
    this.client.publish({
      destination,
      body: JSON.stringify(body),
      headers: this.getAuthHeaders()
    });
  }

  /**
   * Disconnect the WebSocket connection
   */
  disconnect() {
    if (this.client && this.connected) {
      this.client.deactivate();
      this.connected = false;
      this.subscriptions.clear();
    }
  }
}

// Create a singleton instance
const webSocketService = new WebSocketService();

export default webSocketService;