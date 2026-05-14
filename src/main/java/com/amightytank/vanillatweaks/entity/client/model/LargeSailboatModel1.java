// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class large_boat<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "large_boat"), "main");
	private final ModelPart water_patch;
	private final ModelPart visual_root;
	private final ModelPart left;
	private final ModelPart right;
	private final ModelPart front;
	private final ModelPart back;
	private final ModelPart bottom;
	private final ModelPart left_paddle_front;
	private final ModelPart right_paddle_front;
	private final ModelPart left_paddle_middle;
	private final ModelPart right_paddle_middle;
	private final ModelPart right_paddle_back;
	private final ModelPart left_paddle_back;
	private final ModelPart banner_sail_front;
	private final ModelPart banner_panel_front;
	private final ModelPart banner_sail_rear;
	private final ModelPart banner_panel_rear;

	public large_boat(ModelPart root) {
		this.water_patch = root.getChild("water_patch");
		this.visual_root = root.getChild("visual_root");
		this.left = this.visual_root.getChild("left");
		this.right = this.visual_root.getChild("right");
		this.front = this.visual_root.getChild("front");
		this.back = this.visual_root.getChild("back");
		this.bottom = this.visual_root.getChild("bottom");
		this.left_paddle_front = root.getChild("left_paddle_front");
		this.right_paddle_front = root.getChild("right_paddle_front");
		this.left_paddle_middle = root.getChild("left_paddle_middle");
		this.right_paddle_middle = root.getChild("right_paddle_middle");
		this.right_paddle_back = root.getChild("right_paddle_back");
		this.left_paddle_back = root.getChild("left_paddle_back");
		this.banner_sail_front = root.getChild("banner_sail_front");
		this.banner_panel_front = root.getChild("banner_panel_front");
		this.banner_sail_rear = root.getChild("banner_sail_rear");
		this.banner_panel_rear = root.getChild("banner_panel_rear");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 226).addBox(-47.0F, -13.0F, -6.0F, 68.0F, 24.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(228, 178).addBox(21.0F, -12.0F, -6.0F, 3.0F, 22.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(195, 218).addBox(-50.0F, -12.0F, -6.0F, 3.0F, 22.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(205, 218).addBox(24.0F, -11.0F, -6.0F, 3.0F, 20.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(205, 218).addBox(27.0F, -10.0F, -6.0F, 2.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(205, 218).addBox(29.0F, -8.0F, -6.0F, 2.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(205, 218).addBox(31.0F, -6.0F, -6.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(215, 225).addBox(-53.0F, -11.0F, -6.0F, 3.0F, 20.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(215, 225).addBox(-55.0F, -10.0F, -6.0F, 2.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(215, 225).addBox(-57.0F, -8.0F, -6.0F, 2.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(215, 225).addBox(-59.0F, -6.0F, -6.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -6.0F, -1.0F, -1.5708F, 0.0F, 3.1416F));

		PartDefinition visual_root = partdefinition.addOrReplaceChild("visual_root", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition left = visual_root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-46.0F, -7.0F, -1.0F, 68.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(13.0F, -2.0F, -13.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition right = visual_root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 35).mirror().addBox(-22.0F, -7.0F, -1.0F, 68.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-13.0F, -2.0F, -13.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition front = visual_root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(84, 121).addBox(5.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(122, 122).addBox(7.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(112, 95).addBox(8.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(102, 114).addBox(-14.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(112, 114).addBox(-15.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(92, 114).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(122, 82).addBox(3.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(122, 90).addBox(-9.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(122, 114).addBox(-11.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(104, 66).addBox(-7.0F, -7.0F, 11.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(122, 98).addBox(-13.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -2.0F, 34.0F));

		PartDefinition back = visual_root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 123).addBox(5.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 123).addBox(7.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(116, 43).addBox(8.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(116, 52).mirror().addBox(-14.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(28, 107).addBox(-15.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(74, 121).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(92, 123).addBox(3.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(100, 123).addBox(-9.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(108, 123).addBox(-11.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(100, 106).addBox(-7.0F, -7.0F, 11.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(124, 106).addBox(-13.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, -36.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition bottom = visual_root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-46.0F, -13.0F, -3.0F, 68.0F, 24.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 83).addBox(22.0F, -12.0F, -3.0F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(88, 83).addBox(-49.0F, -12.0F, -3.0F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(100, 83).addBox(25.0F, -11.0F, -3.0F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(18, 107).addBox(28.0F, -10.0F, -3.0F, 2.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(54, 110).addBox(30.0F, -8.0F, -3.0F, 2.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(112, 82).addBox(32.0F, -6.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(104, 43).addBox(-52.0F, -11.0F, -3.0F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(8, 107).addBox(-54.0F, -10.0F, -3.0F, 2.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(44, 110).addBox(-56.0F, -8.0F, -3.0F, 2.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(64, 110).addBox(-58.0F, -6.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, -13.0F, 0.0F, 1.5708F, -1.5708F));

		PartDefinition left_paddle_front = partdefinition.addOrReplaceChild("left_paddle_front", CubeListBuilder.create().texOffs(64, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(64, 43).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-24.0F, -8.0F, 12.0F, -0.6409F, 0.6699F, 0.2139F));

		PartDefinition right_paddle_front = partdefinition.addOrReplaceChild("right_paddle_front", CubeListBuilder.create().texOffs(64, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(64, 63).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-24.0F, -8.0F, -12.0F, 2.5007F, 0.6699F, -2.9277F));

		PartDefinition left_paddle_middle = partdefinition.addOrReplaceChild("left_paddle_middle", CubeListBuilder.create().texOffs(64, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(64, 43).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.0F, 12.0F, -0.6409F, 0.6699F, 0.2139F));

		PartDefinition right_paddle_middle = partdefinition.addOrReplaceChild("right_paddle_middle", CubeListBuilder.create().texOffs(64, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(64, 63).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.0F, -12.0F, 2.5007F, 0.6699F, -2.9277F));

		PartDefinition right_paddle_back = partdefinition.addOrReplaceChild("right_paddle_back", CubeListBuilder.create().texOffs(64, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(64, 63).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(26.0F, -8.0F, -12.0F, 2.5007F, 0.6699F, -2.9277F));

		PartDefinition left_paddle_back = partdefinition.addOrReplaceChild("left_paddle_back", CubeListBuilder.create().texOffs(64, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(64, 43).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(26.0F, -8.0F, 12.0F, -0.6409F, 0.6699F, 0.2139F));

		PartDefinition banner_sail_front = partdefinition.addOrReplaceChild("banner_sail_front", CubeListBuilder.create().texOffs(8, 43).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(104, 78).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 80).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition banner_panel_front = partdefinition.addOrReplaceChild("banner_panel_front", CubeListBuilder.create().texOffs(46, 83).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, -32.0F, 6.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition banner_sail_rear = partdefinition.addOrReplaceChild("banner_sail_rear", CubeListBuilder.create().texOffs(0, 43).addBox(-1.0F, -79.0F, -5.0F, 2.0F, 78.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(104, 74).addBox(-7.0F, -79.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 76).addBox(-7.0F, -54.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition banner_panel_rear = partdefinition.addOrReplaceChild("banner_panel_rear", CubeListBuilder.create().texOffs(16, 80).addBox(-13.0F, -44.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -32.0F, 6.0F, 0.0F, -1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		water_patch.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		visual_root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_paddle_front.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_paddle_front.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_paddle_middle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_paddle_middle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_paddle_back.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_paddle_back.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		banner_sail_front.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		banner_panel_front.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		banner_sail_rear.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		banner_panel_rear.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}