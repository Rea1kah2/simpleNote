#!/usr/bin/env python3
"""
SimpleNote Local Sync Server
Serves PWA static assets and provides persistent sync API (/api/data).
Eliminates iOS WebKit sandbox isolation between Safari and Home Screen Standalone PWA.
"""

import http.server
import json
import os
import sys

WEB_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_FILE = os.path.join(WEB_DIR, 'simplenote_store.json')
PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 8080

class SimpleNoteHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=WEB_DIR, **kwargs)

    def end_headers(self):
        # Enable CORS and disable aggressive caching for API
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(204)
        self.end_headers()

    def do_GET(self):
        if self.path == '/api/data':
            self.send_response(200)
            self.send_header('Content-Type', 'application/json; charset=utf-8')
            self.send_header('Cache-Control', 'no-store, no-cache, must-revalidate')
            self.end_headers()

            if os.path.exists(DATA_FILE):
                try:
                    with open(DATA_FILE, 'rb') as f:
                        self.wfile.write(f.read())
                    return
                except Exception as e:
                    print(f"Error reading {DATA_FILE}: {e}")

            # Fallback default empty payload
            default_payload = json.dumps({'expenses': [], 'categories': []}).encode('utf-8')
            self.wfile.write(default_payload)
            return

        super().do_GET()

    def do_POST(self):
        if self.path == '/api/data':
            try:
                content_length = int(self.headers.get('Content-Length', 0))
                body = self.rfile.read(content_length)

                # Validate JSON format
                parsed = json.loads(body.decode('utf-8'))
                if 'expenses' in parsed or 'categories' in parsed:
                    with open(DATA_FILE, 'wb') as f:
                        f.write(body)

                    self.send_response(200)
                    self.send_header('Content-Type', 'application/json; charset=utf-8')
                    self.end_headers()
                    self.wfile.write(b'{"status":"ok","message":"Data persisted successfully"}')
                    return
                else:
                    self.send_response(400)
                    self.end_headers()
                    self.wfile.write(b'{"status":"error","message":"Invalid payload structure"}')
                    return
            except Exception as e:
                print(f"Error handling POST /api/data: {e}")
                self.send_response(500)
                self.end_headers()
                self.wfile.write(b'{"status":"error"}')
                return

        super().do_POST()

def run():
    # Ensure initial data file exists if not present
    if not os.path.exists(DATA_FILE):
        with open(DATA_FILE, 'w', encoding='utf-8') as f:
            json.dump({'expenses': [], 'categories': []}, f, ensure_ascii=False, indent=2)

    server_address = ('', PORT)
    httpd = http.server.ThreadingHTTPServer(server_address, SimpleNoteHandler)
    print(f"SimpleNote Sync Server running on port {PORT} (serving {WEB_DIR})")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nShutting down server.")
        httpd.server_close()

if __name__ == '__main__':
    run()
