import{useState}from'react';
import{Link,useNavigate}from'react-router-dom';
import{api}from'../api';
import{Alert,Field}from'../components/UI';

export default function Signup(){
  const nav=useNavigate();
  const [form,setForm]=useState({username:'',password:'',fullName:'',role:'RECEPTIONIST'});
  const [error,setError]=useState();
  const [loading,setLoading]=useState(false);

  async function submit(e){
    e.preventDefault();
    setLoading(true);
    setError();
    try {
      const data=await api('/auth/signup',{method:'POST',body:JSON.stringify(form)});
      localStorage.setItem('token',data.token);
      localStorage.setItem('user',JSON.stringify(data));
      nav('/');
    } catch (x) {
      setError(x);
    } finally {
      setLoading(false);
    }
  }

  return <div className="login-page">
    <div className="login-art">
      <div className="sun-logo"><i className="bi bi-brightness-high-fill"/></div>
      <h1>Welcome aboard<br/>the clinic team.</h1>
      <p>Create a secure staff account and start managing appointments right away.</p>
      <div className="art-card">
        <i className="bi bi-people-fill"/>
        <span><strong>Staff onboarding</strong><small>Simple account setup for the Sunrise clinic.</small></span>
      </div>
    </div>
    <div className="login-panel">
      <form onSubmit={submit}>
        <span className="eyebrow">NEW ACCOUNT</span>
        <h2>Create account</h2>
        <p className="text-muted">Set up your staff profile to access the clinic system.</p>
        <Alert error={error}/>
        <Field label="Full name"><input className="form-control" value={form.fullName} onChange={e=>setForm({...form,fullName:e.target.value})} required autoFocus/></Field>
        <Field label="Username"><input className="form-control" value={form.username} onChange={e=>setForm({...form,username:e.target.value})} required/></Field>
        <Field label="Password"><input className="form-control" type="password" value={form.password} onChange={e=>setForm({...form,password:e.target.value})} required/></Field>
        <Field label="Role">
          <select className="form-control" value={form.role} onChange={e=>setForm({...form,role:e.target.value})}>
            <option value="RECEPTIONIST">Receptionist</option>
            <option value="DENTIST">Dentist</option>
            <option value="ADMIN">Admin</option>
          </select>
        </Field>
        <button className="btn btn-primary w-100" disabled={loading}>{loading ? 'Creating account…' : 'Create account'} <i className="bi bi-arrow-right"/></button>
        <div className="mt-3 text-center"><Link to="/login">Already have an account? Sign in</Link></div>
      </form>
    </div>
  </div>;
}
