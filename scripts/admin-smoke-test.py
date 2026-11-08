"""Run against a local application with ADMIN_EMAIL / ADMIN_PASSWORD set.
Creates temporary accounts, tests admin workflows, and removes its test data.
"""
import os, re, http.cookiejar, urllib.request, urllib.parse, urllib.error, uuid
BASE=os.environ.get('APP_URL','http://localhost:8080')
def session(): return urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
def get(s,path):
    try:
        r=s.open(BASE+path); return r.status,r.read().decode(),r.url
    except urllib.error.HTTPError as e: return e.code,e.read().decode(),e.url
def post(s,path,data,source=None):
    if source:
        html=get(s,source)[1]
        token=re.search(r'name="_csrf"[^>]*value="([^"]+)"',html)
        assert token, 'CSRF token missing: '+source
        data={**data,'_csrf':token.group(1)}
    try:
        r=s.open(BASE+path,urllib.parse.urlencode(data).encode());return r.status,r.read().decode(),r.url
    except urllib.error.HTTPError as e: return e.code,e.read().decode(),e.url
admin=session(); anon=session()
assert '/login' in get(anon,'/admin/dashboard')[2]
code,html,url=post(admin,'/login',{'username':os.environ['ADMIN_EMAIL'],'password':os.environ['ADMIN_PASSWORD']},'/login')
assert code==200 and 'Admin Management' in html, (code,html[-500:])
assert post(admin,'/admin/vendors/999999/decision',{'status':'APPROVED'})[0]==403
uid=re.search(r'/admin/users/edit/(\d+)',html).group(1)
assert 'cannot delete your own' in post(admin,'/admin/users/delete/'+uid,{},'/admin/dashboard')[1]
marker='admin-test-'+uuid.uuid4().hex[:10]
v=session(); email=marker+'@example.com'; password='TestOnly987!'
data={'businessName':marker,'ownerName':'Test Vendor','email':email,'phone':'0771234567','city':'Colombo','category':'PHOTOGRAPHY','description':'Temporary smoke test','baseRate':'5000','password':password,'confirmPassword':password}
assert post(v,'/vendor/register',data,'/vendor/register')[0]==200
assert 'pending' in post(v,'/vendor/login',{'email':email,'password':password},'/vendor/login')[1]
html=get(admin,'/admin/dashboard?search='+marker)[1]
vid=re.search(r'/admin/vendors/(\d+)/decision',html).group(1)
path='/admin/vendors/'+vid
assert 'Rejection requires' in post(admin,path+'/decision',{'status':'REJECTED','reason':''},'/admin/dashboard')[1]
assert 'decision saved' in post(admin,path+'/decision',{'status':'APPROVED'},'/admin/dashboard')[1]
assert '/vendor/workboard' in post(v,'/vendor/login',{'email':email,'password':password},'/vendor/login')[2]
assert get(admin,path+'/edit')[0]==200
assert 'Vendor updated' in post(admin,path+'/edit',data,path+'/edit')[1]
assert 'decision saved' in post(admin,path+'/decision',{'status':'REJECTED','reason':'Test rejection'},'/admin/dashboard')[1]
assert '/vendor/login' in get(v,'/vendor/workboard')[2]
assert 'Test rejection' in post(v,'/vendor/login',{'email':email,'password':password},'/vendor/login')[1]
assert 'Vendor deleted' in post(admin,path+'/delete',{},'/admin/dashboard')[1]
c=session(); ce=marker+'-couple@example.com'
assert post(c,'/register',{'fullName':marker,'email':ce,'phoneNumber':'0771234567','password':password},'/register')[0]==200
assert '/customer/dashboard' in post(c,'/login',{'username':ce,'password':password},'/login')[2]
assert get(c,'/admin/dashboard')[0]==403
html=get(admin,'/admin/dashboard?search='+marker)[1]
uid=re.search(r'/admin/users/edit/(\d+)',html).group(1)
u='/admin/users/edit/'+uid
assert get(admin,u)[0]==200
assert 'valid email' in post(admin,u,{'fullName':marker,'email':'invalid','phoneNumber':'123'},u)[1]
assert 'User updated' in post(admin,u,{'fullName':marker+' updated','email':ce,'phoneNumber':'0771234567','password':''},u)[1]
assert 'User deleted' in post(admin,'/admin/users/delete/'+uid,{},'/admin/dashboard')[1]
assert '/login' in get(c,'/customer/dashboard')[2]
assert 'USER_DELETED' in get(admin,'/admin/activity')[1]
assert 'Recent platform activity' in get(admin,'/admin/activity')[1]
print('PASS: anonymous/customer access, CSRF, self-delete protection, vendor registration/approval/rejection/session revocation/edit/delete, user validation/edit/delete, activity and template rendering')
