1. Generate Operator scaffold

```
operator-sdk init --repo=github.com/leszko/hazelcast-operator
operator-sdk create api --version v1 --group=hazelcast --kind Hazelcast --resource=true --controller=true
```

2. Add logic to create Deployment resources `Hazelcast` is created

Update `controllers/hazelcast_controller.go`

Section `import`
```
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
```

Function `Reconcile` and others.
```
// +kubebuilder:rbac:groups=cache.example.com,resources=memcacheds,verbs=get;list;watch;create;update;patch;delete
// +kubebuilder:rbac:groups=cache.example.com,resources=memcacheds/status,verbs=get;update;patch
// +kubebuilder:rbac:groups=apps,resources=deployments,verbs=get;list;watch;create;update;patch;delete
// +kubebuilder:rbac:groups=core,resources=pods,verbs=get;list;watch

func (r *HazelcastReconciler) Reconcile(req ctrl.Request) (ctrl.Result, error) {
	ctx := context.Background()
	log := r.Log.WithValues("hazelcast", req.NamespacedName)

	// Fetch the Hazelcast instance
	hazelcast := &hazelcastv1.Hazelcast{}
	err := r.Get(ctx, req.NamespacedName, hazelcast)
	if err != nil {
		if errors.IsNotFound(err) {
			// Request object not found, could have been deleted after reconcile request.
			// Owned objects are automatically garbage collected. For additional cleanup logic use finalizers.
			// Return and don't requeue
			log.Info("Hazelcast resource not found. Ignoring since object must be deleted")
			return ctrl.Result{}, nil
		}
		// Error reading the object - requeue the request.
		log.Error(err, "Failed to get Hazelcast")
		return ctrl.Result{}, err
	}

	// Check if the deployment already exists, if not create a new one
	found := &appsv1.Deployment{}
	err = r.Get(ctx, types.NamespacedName{Name: hazelcast.Name, Namespace: hazelcast.Namespace}, found)
	if err != nil && errors.IsNotFound(err) {
		// Define a new deployment
		dep := r.deploymentForHazelcast(hazelcast)
		log.Info("Creating a new Deployment", "Deployment.Namespace", dep.Namespace, "Deployment.Name", dep.Name)
		err = r.Create(ctx, dep)
		if err != nil {
			log.Error(err, "Failed to create new Deployment", "Deployment.Namespace", dep.Namespace, "Deployment.Name", dep.Name)
			return ctrl.Result{}, err
		}
		// Deployment created successfully - return and requeue
		return ctrl.Result{Requeue: true}, nil
	} else if err != nil {
		log.Error(err, "Failed to get Deployment")
		return ctrl.Result{}, err
	}

	// Update the Hazelcast status with the pod names
	// List the pods for this hazelcast's deployment
	podList := &corev1.PodList{}
	listOpts := []client.ListOption{
		client.InNamespace(hazelcast.Namespace),
		client.MatchingLabels(labelsForHazelcast(hazelcast.Name)),
	}
	if err = r.List(ctx, podList, listOpts...); err != nil {
		log.Error(err, "Failed to list pods", "Hazelcast.Namespace", hazelcast.Namespace, "Hazelcast.Name", hazelcast.Name)
		return ctrl.Result{}, err
	}
	podNames := getPodNames(podList.Items)

	// Update status.Nodes if needed
	if !reflect.DeepEqual(podNames, hazelcast.Status.Nodes) {
		hazelcast.Status.Nodes = podNames
		err := r.Status().Update(ctx, hazelcast)
		if err != nil {
			log.Error(err, "Failed to update Hazelcast status")
			return ctrl.Result{}, err
		}
	}

	return ctrl.Result{}, nil
}

// deploymentForHazelcast returns a hazelcast Deployment object
func (r *HazelcastReconciler) deploymentForHazelcast(m *hazelcastv1.Hazelcast) *appsv1.Deployment {
	ls := labelsForHazelcast(m.Name)

	dep := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      m.Name,
			Namespace: m.Namespace,
		},
		Spec: appsv1.DeploymentSpec{
			Selector: &metav1.LabelSelector{
				MatchLabels: ls,
			},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: ls,
				},
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{{
						Image:   "hazelcast/hazelcast:4.0",
						Name:    "hazelcast",
					}},
				},
			},
		},
	}
	// Set Hazelcast instance as the owner and controller
	ctrl.SetControllerReference(m, dep, r.Scheme)
	return dep
}

// labelsForHazelcast returns the labels for selecting the resources
// belonging to the given hazelcast CR name.
func labelsForHazelcast(name string) map[string]string {
	return map[string]string{"app": "hazelcast", "hazelcast_cr": name}
}

// getPodNames returns the pod names of the array of pods passed in
func getPodNames(pods []corev1.Pod) []string {
	var podNames []string
	for _, pod := range pods {
		podNames = append(podNames, pod.Name)
	}
	return podNames
}

func (r *HazelcastReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&hazelcastv1.Hazelcast{}).
		Owns(&appsv1.Deployment{}).
		Complete(r)
}
```

3. Build and Push the Operator

```
docker build -t leszko/hazelcast-operator:go . && docker push leszko/hazelcast-operator:go
```

4. Install Operator

```
make install
make deploy IMG=leszko/hazelcast-operator:go
```

5. Check that the operator is running

```
kubectl logs deployment.apps/hazelcast-operator-controller-manager -n hazelcast-operator-system -c manager
```

6. Install resource (Hazelcast cluster)

```
kubectl apply -f config/samples/hazelcast_v1_hazelcast.yaml
```

7. Present that Hazelcast is running

8. Uninstall

```
kubectl delete -f config/samples/hazelcast_v1_hazelcast.yaml
kustomize build config/default | kubectl delete -f -
```