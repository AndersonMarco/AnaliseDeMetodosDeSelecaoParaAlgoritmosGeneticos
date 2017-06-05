import matplotlib as mpl
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import matplotlib.pyplot as plt
dt=0.0001
def eulerMethod(eqs,vIni,ite,dt):
    ret=[]
    vn=[]
    vn1=[]
    viteracoes=[]
    nvariables=len(vIni)
    viteracoes.append([])
    for i in range(nvariables):
        viteracoes.append([])
        vn.append(vIni[i])
        vn1.append(0)

    for i in range(ite):        
        for j in range(nvariables):
            vn1[j]=vn[j]+(eqs[j](vn)*dt)
        
        viteracoes[0].append(float(i))
        for j in range(nvariables):
            viteracoes[j+1].append(vn1[j])
            vn[j]=vn1[j]
    
    return viteracoes

lorenz=[lambda v: (lambda v,o: (v[1]-v[0])*o)(v,10.0),
        lambda v: (lambda v,p: (v[0]*(p-v[2]))-v[1])(v,28.0),
        lambda v:(lambda v,b: ((v[0]*v[1])-(b*v[2])))(v,2.6666666) ]

s=eulerMethod(lorenz ,[5.3,7.8,11.20001],700000,dt)
sRec=[[],[],[]]
r=float(dt*6200000.0);
i=0
ind1=0
ind2=0
ind3=0
while(ind3<len(s[0])):
    ind1=int(round(float(i)*r))
    ind2=int(round(float(i+1)*r))
    ind3=int(round(float(i+2)*r))
    sRec[0].append(s[1][ind1])
    sRec[1].append(s[1][ind2])
    sRec[2].append(s[1][ind3])
    i=i+1
    ind3=int(round(float(i+2)*r))
    
    
mpl.rcParams['legend.fontsize'] = 10

fig = plt.figure()
ax = fig.gca(projection='3d')
theta = np.linspace(-4 * np.pi, 4 * np.pi, 100)

ax.plot(sRec[0], sRec[1], sRec[2], label='parametric curve')
#ax.plot(s[1], s[2], s[3], label='parametric curve')
ax.legend()

plt.show()
