# 🚨 ReGenesis Raspberry Pi Backend - Emergency Recovery Guide

## Quick Diagnosis Checklist

### 1. Is the Pi Powered On?
```bash
# From your computer, try to ping it
ping raspberrypi.local
# or use its IP address
ping 192.168.x.x
```

**If no response:** Check power cable, LED indicators, and network connection.

---

## 2. Can You SSH into the Pi?

```bash
ssh pi@raspberrypi.local
# or
ssh pi@192.168.x.x
```

**If SSH fails:**
- Verify SSH is enabled: Check `raspi-config` on the Pi
- Try default password: `raspberry`
- Check hostname is correct

---

## 3. Is the Genesis Backend Service Running?

Once SSH'd into the Pi, run:

```bash
# Check service status
sudo systemctl status genesis-backend.service

# If it's not running, start it
sudo systemctl start genesis-backend.service

# Check if port 5000 is listening
ss -tlnp | grep 5000

# View service logs
sudo journalctl -u genesis-backend.service -f
```

---

## 4. First Time Setup (if not already done)

### Step 1: Copy the setup script to the Pi
```bash
# From your dev machine
scp app/ai_backend/rpi-setup.sh pi@raspberrypi.local:~/setup.sh

# SSH in
ssh pi@raspberrypi.local
```

### Step 2: Run setup on the Pi
```bash
cd ~/A.u.r.a.k.a.i_ReGenesis/app/ai_backend
bash ~/setup.sh
```

This will:
- ✅ Install Python dependencies
- ✅ Create systemd service
- ✅ Auto-start on boot
- ✅ Start the service immediately

---

## 5. Manual Service Recovery

### Stop the service
```bash
sudo systemctl stop genesis-backend.service
```

### Check if python process exists
```bash
ps aux | grep genesis_web_server.py
# If stuck, kill it
pkill -f genesis_web_server.py
```

### Restart the service
```bash
sudo systemctl restart genesis-backend.service
sleep 3
sudo systemctl status genesis-backend.service
```

### Run manually for debugging
```bash
cd ~/A.u.r.a.k.a.i_ReGenesis/app/ai_backend
source venv/bin/activate
python3 genesis_web_server.py
```

---

## 6. Common Issues & Solutions

### ❌ "Connection refused" on port 5000
```bash
# The service isn't running or crashed
sudo systemctl start genesis-backend.service
sudo journalctl -u genesis-backend.service -n 50 --no-pager
```

### ❌ "ModuleNotFoundError" or missing dependencies
```bash
# Reinstall dependencies
cd ~/A.u.r.a.k.a.i_ReGenesis/app/ai_backend
source venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt
sudo systemctl restart genesis-backend.service
```

### ❌ "Permission denied" errors
```bash
# Fix permissions on the directory
sudo chown -R pi:pi ~/A.u.r.a.k.a.i_ReGenesis
chmod -R u+rwx ~/A.u.r.a.k.a.i_ReGenesis/app/ai_backend
```

### ❌ Out of disk space
```bash
# Check disk usage
df -h
df -h ~

# If full, clean up:
sudo apt clean
sudo apt autoclean
```

### ❌ Out of memory
```bash
# Check memory
free -h
ps aux --sort=-%mem | head -10

# Restart to free memory
sudo reboot
```

### ❌ Service keeps crashing
```bash
# View detailed logs
sudo journalctl -u genesis-backend.service -e --no-pager

# Try running in foreground to see errors
cd ~/A.u.r.a.k.a.i_ReGenesis/app/ai_backend
source venv/bin/activate
python3 genesis_web_server.py 2>&1 | head -100
```

---

## 7. Testing the Backend

Once service is running:

```bash
# From any computer (replace IP with your Pi's IP)
PI_IP="192.168.x.x"

# Health check
curl http://$PI_IP:5000/status

# Conference stream (will stream events)
curl http://$PI_IP:5000/genesis/conference/stream

# WebSocket test (requires wscat or similar tool)
# npm install -g wscat
# wscat -c ws://$PI_IP:5000/api/conference/ws/default
```

Expected responses:
- `/status` → `{"status": "LDO Collective Active", "gate": "Veto Operational"}`
- `/genesis/conference/stream` → Event stream (text/event-stream)

---

## 8. Permanent Fix: Enable Auto-Start on Boot

Already done by `rpi-setup.sh`, but verify:

```bash
# Check if service auto-starts
sudo systemctl is-enabled genesis-backend.service
# Should output: enabled

# If not, enable it
sudo systemctl enable genesis-backend.service
```

---

## 9. Monitor in Real-Time

```bash
# Watch service logs as they appear
sudo journalctl -u genesis-backend.service -f

# Watch system resource usage
watch -n 1 'free -h && ps aux | grep genesis'
```

---

## 10. Final Verification

Once recovered, verify end-to-end:

```bash
# 1. Service is running
sudo systemctl status genesis-backend.service

# 2. Port is listening
ss -tlnp | grep 5000

# 3. Process exists
pgrep -f genesis_web_server.py

# 4. Can reach it
curl http://localhost:5000/status
```

All should return ✅ success.

---

## 📞 Emergency Contact

If the Pi still won't start:

1. **Power cycle:** Unplug for 10 seconds, plug back in
2. **SSH to Pi and run:** `sudo systemctl status genesis-backend.service`
3. **Check logs:** `sudo journalctl -u genesis-backend.service -n 100`
4. **Last resort:** `sudo reboot`

---

**Status:** Backend ready to go. 🚀

