import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api";
import { Alert, Field } from "../components/UI";
export default function Login() {
  const nav = useNavigate(),
    [form, setForm] = useState({ username: "admin", password: "Admin@123" }),
    [error, setError] = useState(),
    [loading, setLoading] = useState(false);
  async function submit(e) {
    e.preventDefault();
    setLoading(true);
    setError();
    try {
      const data = await api("/auth/login", {
        method: "POST",
        body: JSON.stringify(form),
      });
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data));
      nav("/");
    } catch (x) {
      setError(x);
    } finally {
      setLoading(false);
    }
  }
  return (
    <div className="login-page">
      <div className="login-art">
        <div className="sun-logo">
          <i className="bi bi-brightness-high-fill" />
        </div>
        <h1>
          Healthy smiles
          <br />
          start here.
        </h1>
        <p>Simple, thoughtful dental care management for the Sunrise team.</p>
        <div className="art-card">
          <i className="bi bi-shield-check" />
          <span>
            <strong>Secure staff access</strong>
            <small>Your clinic data stays protected.</small>
          </span>
        </div>
      </div>
      <div className="login-panel">
        <form onSubmit={submit}>
          <span className="eyebrow">STAFF PORTAL</span>
          <h2>Welcome back</h2>
          <p className="text-muted">Sign in to manage today's clinic.</p>
          <Alert error={error} />
          <Field label="Username">
            <input
              className="form-control"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
              autoFocus
            />
          </Field>
          <Field label="Password">
            <input
              className="form-control"
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </Field>
          <button className="btn btn-primary w-100" disabled={loading}>
            {loading ? "Signing in…" : "Sign in"}{" "}
            <i className="bi bi-arrow-right" />
          </button>
          <div className="mt-3 text-center">
            <Link to="/signup">Create an account</Link>
          </div>
          <small className="demo-hint">Demo: admin / Admin@123</small>
        </form>
      </div>
    </div>
  );
}
