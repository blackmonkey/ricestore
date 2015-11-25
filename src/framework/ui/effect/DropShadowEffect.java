
package framework.ui.effect;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import framework.ui.component.Rect;
import framework.util.DrawUtil;

/**
 * By using Gaussian blurring, draw a blur drop shadow for image or text.
 * <p>
 * See details of Gaussian blurring at <a
 * href="http://en.wikipedia.org/wiki/Gaussian_blur"
 * >http://en.wikipedia.org/wiki/Gaussian_blur</a>.
 * <p>
 * To optimize Gaussian blurring in this concrete project, for a good enough
 * blur drop shadow, we firm the shadow blur radius as 5, and the
 * horizontal/vertical offsets as 1. There for, we got firmed Gaussian blur
 * kernel:
 *
 * <pre>
 * float kernel[11] = {
 *     1.4681699E-6,
 *     1.3791217E-4,
 *     0.0045689577,
 *     0.053275038,
 *     0.246184,
 *     0.39166525,
 *     0.246184,
 *     0.053275038,
 *     0.0045689577,
 *     1.3791217E-4,
 *     1.4681699E-6
 * };
 * </pre>
 *
 * For drawing shadow, we only handle alpha except RGB color values, therefore,
 * to speed up calculating, we can cache the product matrix of alpha value and
 * Gaussian blur kernel. See {@link #KERNEL_MATRIX}.
 * <p>
 * @author Oscar Cai
 */

public class DropShadowEffect extends Effect {
    /**
     * Firmed shadow horizontal/vertical offset.
     */
    private static final int SHADOW_X_OFFSET = 1;
    private static final int SHADOW_Y_OFFSET = 1;

    /**
     * Firmed shadow radius.
     */
    private static final int SHADOW_BLUR_RADIUS = 5;

    /**
     * The availabe margin between the bounds of hard shadow and soft shadow,
     * depends on {@link #SHADOW_BLUR_RADIUS}. If {@link #SHADOW_BLUR_RADIUS} is
     * 6, {@link #SHADOW_BLUR_MARGIN} is 5.
     */
    private static final int SHADOW_BLUR_MARGIN = 3;

    /**
     * Firmed product matrix of alpha value and Gaussian blur kernel:
     * KERNEL_MATRIX[kernel_x][alpha] = kernel(x) * alpha;
     */
    private static final float[][] KERNEL_MATRIX = new float[][] {
        {
            0.0f,0.0045689577f,0.009137915f,0.013706873f,0.01827583f,0.022844788f,0.027413746f,0.031982705f,
            0.03655166f,0.04112062f,0.045689575f,0.050258536f,0.054827493f,0.05939645f,0.06396541f,0.06853437f,
            0.07310332f,0.07767228f,0.08224124f,0.086810194f,0.09137915f,0.095948115f,0.10051707f,0.10508603f,
            0.109654985f,0.11422394f,0.1187929f,0.123361856f,0.12793082f,0.13249977f,0.13706873f,0.14163768f,
            0.14620665f,0.15077561f,0.15534456f,0.15991352f,0.16448247f,0.16905144f,0.17362039f,0.17818935f,
            0.1827583f,0.18732727f,0.19189623f,0.19646518f,0.20103414f,0.2056031f,0.21017206f,0.214741f,
            0.21930997f,0.22387893f,0.22844788f,0.23301685f,0.2375858f,0.24215476f,0.24672371f,0.25129268f,
            0.25586164f,0.2604306f,0.26499954f,0.2695685f,0.27413747f,0.27870643f,0.28327537f,0.28784433f,
            0.2924133f,0.29698226f,0.30155122f,0.30612016f,0.31068912f,0.3152581f,0.31982705f,0.32439598f,
            0.32896495f,0.3335339f,0.33810288f,0.34267184f,0.34724078f,0.35180974f,0.3563787f,0.36094767f,
            0.3655166f,0.37008557f,0.37465453f,0.3792235f,0.38379246f,0.3883614f,0.39293036f,0.39749932f,
            0.4020683f,0.40663725f,0.4112062f,0.41577515f,0.4203441f,0.42491308f,0.429482f,0.43405098f,
            0.43861994f,0.4431889f,0.44775787f,0.4523268f,0.45689577f,0.46146473f,0.4660337f,0.47060263f,
            0.4751716f,0.47974056f,0.48430952f,0.4888785f,0.49344742f,0.4980164f,0.50258535f,0.5071543f,
            0.5117233f,0.5162922f,0.5208612f,0.52543014f,0.5299991f,0.5345681f,0.539137f,0.54370594f,
            0.54827493f,0.55284387f,0.55741286f,0.5619818f,0.56655073f,0.5711197f,0.57568866f,0.58025765f,
            0.5848266f,0.5893955f,0.5939645f,0.59853345f,0.60310245f,0.6076714f,0.6122403f,0.6168093f,
            0.62137824f,0.62594724f,0.6305162f,0.6350851f,0.6396541f,0.64422303f,0.64879197f,0.65336096f,
            0.6579299f,0.6624989f,0.6670678f,0.67163676f,0.67620575f,0.6807747f,0.6853437f,0.6899126f,
            0.69448155f,0.69905055f,0.7036195f,0.7081885f,0.7127574f,0.71732634f,0.72189534f,0.7264643f,
            0.7310332f,0.7356022f,0.74017113f,0.7447401f,0.74930906f,0.753878f,0.758447f,0.7630159f,
            0.7675849f,0.77215385f,0.7767228f,0.7812918f,0.7858607f,0.7904297f,0.79499865f,0.7995676f,
            0.8041366f,0.8087055f,0.8132745f,0.81784344f,0.8224124f,0.82698137f,0.8315503f,0.83611923f,
            0.8406882f,0.84525716f,0.84982616f,0.8543951f,0.858964f,0.863533f,0.86810195f,0.87267095f,
            0.8772399f,0.8818088f,0.8863778f,0.89094675f,0.89551574f,0.9000847f,0.9046536f,0.9092226f,
            0.91379154f,0.91836053f,0.92292947f,0.9274984f,0.9320674f,0.9366363f,0.94120526f,0.94577426f,
            0.9503432f,0.9549122f,0.9594811f,0.96405005f,0.96861905f,0.973188f,0.977757f,0.9823259f,
            0.98689485f,0.99146384f,0.9960328f,1.0006018f,1.0051707f,1.0097396f,1.0143086f,1.0188776f,
            1.0234466f,1.0280155f,1.0325844f,1.0371534f,1.0417224f,1.0462914f,1.0508603f,1.0554292f,
            1.0599982f,1.0645672f,1.0691361f,1.0737051f,1.078274f,1.082843f,1.0874119f,1.0919809f,
            1.0965499f,1.1011188f,1.1056877f,1.1102567f,1.1148257f,1.1193947f,1.1239636f,1.1285325f,
            1.1331015f,1.1376705f,1.1422395f,1.1468084f,1.1513773f,1.1559463f,1.1605153f,1.1650842f
        },
        {
            0.0f,0.053275038f,0.106550075f,0.15982512f,0.21310015f,0.26637518f,0.31965023f,0.37292525f,
            0.4262003f,0.47947535f,0.53275037f,0.5860254f,0.63930047f,0.6925755f,0.7458505f,0.79912555f,
            0.8524006f,0.90567565f,0.9589507f,1.0122257f,1.0655007f,1.1187758f,1.1720508f,1.2253258f,
            1.2786009f,1.3318759f,1.385151f,1.438426f,1.491701f,1.5449761f,1.5982511f,1.6515262f,
            1.7048012f,1.7580762f,1.8113513f,1.8646263f,1.9179014f,1.9711764f,2.0244515f,2.0777264f,
            2.1310015f,2.1842766f,2.2375517f,2.2908266f,2.3441017f,2.3973768f,2.4506516f,2.5039268f,
            2.5572019f,2.6104767f,2.6637518f,2.717027f,2.770302f,2.823577f,2.876852f,2.9301271f,
            2.983402f,3.0366771f,3.0899522f,3.143227f,3.1965022f,3.2497773f,3.3030524f,3.3563273f,
            3.4096024f,3.4628775f,3.5161524f,3.5694275f,3.6227026f,3.6759777f,3.7292526f,3.7825277f,
            3.8358028f,3.8890777f,3.9423528f,3.9956279f,4.048903f,4.102178f,4.1554527f,4.208728f,
            4.262003f,4.315278f,4.368553f,4.4218283f,4.4751034f,4.528378f,4.581653f,4.634928f,
            4.6882033f,4.7414784f,4.7947536f,4.848028f,4.9013033f,4.9545784f,5.0078535f,5.0611286f,
            5.1144037f,5.167679f,5.2209535f,5.2742286f,5.3275037f,5.380779f,5.434054f,5.487329f,
            5.540604f,5.5938787f,5.647154f,5.700429f,5.753704f,5.806979f,5.8602543f,5.9135294f,
            5.966804f,6.020079f,6.0733542f,6.1266294f,6.1799045f,6.2331796f,6.286454f,6.3397293f,
            6.3930044f,6.4462795f,6.4995546f,6.5528297f,6.606105f,6.6593795f,6.7126546f,6.7659297f,
            6.819205f,6.87248f,6.925755f,6.97903f,7.032305f,7.08558f,7.138855f,7.19213f,
            7.245405f,7.2986803f,7.3519554f,7.40523f,7.458505f,7.5117803f,7.5650554f,7.6183305f,
            7.6716056f,7.72488f,7.7781553f,7.8314304f,7.8847055f,7.9379807f,7.9912558f,8.044531f,
            8.097806f,8.151081f,8.204356f,8.25763f,8.310905f,8.364181f,8.417456f,8.470731f,
            8.524006f,8.577281f,8.630556f,8.683831f,8.737106f,8.790381f,8.843657f,8.896932f,
            8.950207f,9.003481f,9.056756f,9.110031f,9.163306f,9.216581f,9.269856f,9.323132f,
            9.376407f,9.429682f,9.482957f,9.536232f,9.589507f,9.642782f,9.696056f,9.749331f,
            9.802607f,9.855882f,9.909157f,9.962432f,10.015707f,10.068982f,10.122257f,10.175532f,
            10.228807f,10.282083f,10.335358f,10.388633f,10.441907f,10.495182f,10.548457f,10.601732f,
            10.655007f,10.708282f,10.761558f,10.814833f,10.868108f,10.921383f,10.974658f,11.027933f,
            11.081208f,11.134482f,11.1877575f,11.241033f,11.294308f,11.347583f,11.400858f,11.454133f,
            11.507408f,11.560683f,11.613958f,11.667233f,11.720509f,11.773784f,11.827059f,11.880333f,
            11.933608f,11.986883f,12.040158f,12.093433f,12.1467085f,12.199984f,12.253259f,12.306534f,
            12.359809f,12.413084f,12.466359f,12.519634f,12.572908f,12.6261835f,12.679459f,12.732734f,
            12.786009f,12.839284f,12.892559f,12.945834f,12.999109f,13.052384f,13.1056595f,13.158935f,
            13.21221f,13.265485f,13.318759f,13.372034f,13.425309f,13.478584f,13.531859f,13.5851345f
        },
        {
            0.0f,0.246184f,0.492368f,0.73855203f,0.984736f,1.2309201f,1.4771041f,1.723288f,
            1.969472f,2.215656f,2.4618402f,2.708024f,2.9542081f,3.200392f,3.446576f,3.69276f,
            3.938944f,4.185128f,4.431312f,4.677496f,4.9236803f,5.169864f,5.416048f,5.662232f,
            5.9084163f,6.1546f,6.400784f,6.6469684f,6.893152f,7.139336f,7.38552f,7.6317043f,
            7.877888f,8.124072f,8.370256f,8.61644f,8.862624f,9.1088085f,9.354992f,9.601176f,
            9.847361f,10.093544f,10.339728f,10.585913f,10.832096f,11.07828f,11.324464f,11.570648f,
            11.816833f,12.063016f,12.3092f,12.555385f,12.801568f,13.047752f,13.293937f,13.54012f,
            13.786304f,14.032489f,14.278672f,14.524857f,14.77104f,15.017224f,15.263409f,15.509592f,
            15.755776f,16.00196f,16.248144f,16.494328f,16.740513f,16.986696f,17.23288f,17.479065f,
            17.725248f,17.971432f,18.217617f,18.4638f,18.709984f,18.95617f,19.202353f,19.448536f,
            19.694721f,19.940905f,20.187088f,20.433273f,20.679457f,20.92564f,21.171825f,21.418009f,
            21.664192f,21.910378f,22.15656f,22.402744f,22.648928f,22.895113f,23.141296f,23.38748f,
            23.633665f,23.879848f,24.126032f,24.372217f,24.6184f,24.864584f,25.11077f,25.356953f,
            25.603136f,25.849321f,26.095505f,26.341688f,26.587873f,26.834057f,27.08024f,27.326426f,
            27.572609f,27.818792f,28.064978f,28.311161f,28.557344f,28.803528f,29.049713f,29.295897f,
            29.54208f,29.788265f,30.034449f,30.280632f,30.526817f,30.773f,31.019184f,31.26537f,
            31.511553f,31.757736f,32.00392f,32.250103f,32.49629f,32.742474f,32.988655f,33.23484f,
            33.481026f,33.727207f,33.973392f,34.219578f,34.46576f,34.711945f,34.95813f,35.20431f,
            35.450497f,35.696682f,35.942863f,36.18905f,36.435234f,36.681416f,36.9276f,37.173786f,
            37.419968f,37.666153f,37.91234f,38.15852f,38.404705f,38.65089f,38.89707f,39.143257f,
            39.389442f,39.635624f,39.88181f,40.127995f,40.374176f,40.62036f,40.866547f,41.11273f,
            41.358913f,41.6051f,41.85128f,42.097466f,42.34365f,42.589832f,42.836018f,43.082203f,
            43.328384f,43.57457f,43.820755f,44.066936f,44.31312f,44.559303f,44.80549f,45.051674f,
            45.297855f,45.54404f,45.790226f,46.036407f,46.282593f,46.528778f,46.77496f,47.021145f,
            47.26733f,47.51351f,47.759697f,48.005882f,48.252064f,48.49825f,48.744434f,48.990616f,
            49.2368f,49.482986f,49.729168f,49.975353f,50.22154f,50.46772f,50.713905f,50.96009f,
            51.206272f,51.452457f,51.698643f,51.944824f,52.19101f,52.437195f,52.683376f,52.92956f,
            53.175747f,53.42193f,53.668114f,53.9143f,54.16048f,54.406666f,54.65285f,54.899033f,
            55.145218f,55.391403f,55.637585f,55.88377f,56.129955f,56.376137f,56.622322f,56.868504f,
            57.11469f,57.360874f,57.607056f,57.85324f,58.099426f,58.345608f,58.591793f,58.83798f,
            59.08416f,59.330345f,59.57653f,59.822712f,60.068897f,60.315083f,60.561264f,60.80745f,
            61.053635f,61.299816f,61.546f,61.792187f,62.03837f,62.284554f,62.53074f,62.77692f
        },
        {
            0.0f,0.39166525f,0.7833305f,1.1749958f,1.566661f,1.9583262f,2.3499916f,2.7416568f,
            3.133322f,3.5249872f,3.9166524f,4.3083177f,4.699983f,5.091648f,5.4833136f,5.8749785f,
            6.266644f,6.6583095f,7.0499744f,7.44164f,7.833305f,8.22497f,8.616635f,9.008301f,
            9.399966f,9.791632f,10.183296f,10.574962f,10.966627f,11.358293f,11.749957f,12.141623f,
            12.533288f,12.924953f,13.316619f,13.708283f,14.099949f,14.491614f,14.88328f,15.274944f,
            15.66661f,16.058275f,16.44994f,16.841606f,17.23327f,17.624937f,18.016602f,18.408266f,
            18.799932f,19.191597f,19.583263f,19.974928f,20.366592f,20.758259f,21.149923f,21.541588f,
            21.933254f,22.324919f,22.716585f,23.10825f,23.499914f,23.89158f,24.283245f,24.674911f,
            25.066576f,25.45824f,25.849907f,26.241571f,26.633238f,27.024902f,27.416567f,27.808233f,
            28.199898f,28.591564f,28.983229f,29.374893f,29.76656f,30.158224f,30.549889f,30.941555f,
            31.33322f,31.724886f,32.11655f,32.508217f,32.89988f,33.291546f,33.683212f,34.074875f,
            34.46654f,34.858208f,35.249874f,35.641537f,36.033203f,36.42487f,36.816532f,37.2082f,
            37.599865f,37.991528f,38.383194f,38.77486f,39.166527f,39.55819f,39.949856f,40.341522f,
            40.733185f,41.12485f,41.516518f,41.90818f,42.299847f,42.691513f,43.083176f,43.474842f,
            43.86651f,44.258175f,44.649837f,45.041504f,45.43317f,45.824833f,46.2165f,46.608166f,
            46.99983f,47.391495f,47.78316f,48.174828f,48.56649f,48.958157f,49.349823f,49.741486f,
            50.133152f,50.52482f,50.91648f,51.308147f,51.699814f,52.091476f,52.483143f,52.87481f,
            53.266476f,53.65814f,54.049805f,54.44147f,54.833134f,55.2248f,55.616467f,56.00813f,
            56.399796f,56.791462f,57.18313f,57.57479f,57.966457f,58.358124f,58.749786f,59.141453f,
            59.53312f,59.92478f,60.31645f,60.708115f,61.099777f,61.491444f,61.88311f,62.274776f,
            62.66644f,63.058105f,63.44977f,63.841434f,64.2331f,64.62476f,65.01643f,65.4081f,
            65.79976f,66.19143f,66.58309f,66.974754f,67.366425f,67.75809f,68.14975f,68.54142f,
            68.93308f,69.32475f,69.716415f,70.10808f,70.49975f,70.89141f,71.28307f,71.67474f,
            72.06641f,72.45807f,72.84974f,73.2414f,73.633064f,74.024734f,74.4164f,74.80806f,
            75.19973f,75.59139f,75.983055f,76.374725f,76.76639f,77.15805f,77.54972f,77.94138f,
            78.33305f,78.72472f,79.11638f,79.50805f,79.89971f,80.291374f,80.683044f,81.07471f,
            81.46637f,81.85804f,82.2497f,82.641365f,83.033035f,83.4247f,83.81636f,84.20803f,
            84.59969f,84.991356f,85.383026f,85.77469f,86.16635f,86.55802f,86.949684f,87.341354f,
            87.73302f,88.12468f,88.51635f,88.90801f,89.299675f,89.691345f,90.08301f,90.47467f,
            90.86634f,91.258f,91.649666f,92.041336f,92.433f,92.82466f,93.21633f,93.607994f,
            93.99966f,94.39133f,94.78299f,95.17465f,95.56632f,95.957985f,96.349655f,96.74132f,
            97.13298f,97.52465f,97.91631f,98.307976f,98.699646f,99.09131f,99.48297f,99.87464f
        },
        {
            0.0f,0.246184f,0.492368f,0.73855203f,0.984736f,1.2309201f,1.4771041f,1.723288f,
            1.969472f,2.215656f,2.4618402f,2.708024f,2.9542081f,3.200392f,3.446576f,3.69276f,
            3.938944f,4.185128f,4.431312f,4.677496f,4.9236803f,5.169864f,5.416048f,5.662232f,
            5.9084163f,6.1546f,6.400784f,6.6469684f,6.893152f,7.139336f,7.38552f,7.6317043f,
            7.877888f,8.124072f,8.370256f,8.61644f,8.862624f,9.1088085f,9.354992f,9.601176f,
            9.847361f,10.093544f,10.339728f,10.585913f,10.832096f,11.07828f,11.324464f,11.570648f,
            11.816833f,12.063016f,12.3092f,12.555385f,12.801568f,13.047752f,13.293937f,13.54012f,
            13.786304f,14.032489f,14.278672f,14.524857f,14.77104f,15.017224f,15.263409f,15.509592f,
            15.755776f,16.00196f,16.248144f,16.494328f,16.740513f,16.986696f,17.23288f,17.479065f,
            17.725248f,17.971432f,18.217617f,18.4638f,18.709984f,18.95617f,19.202353f,19.448536f,
            19.694721f,19.940905f,20.187088f,20.433273f,20.679457f,20.92564f,21.171825f,21.418009f,
            21.664192f,21.910378f,22.15656f,22.402744f,22.648928f,22.895113f,23.141296f,23.38748f,
            23.633665f,23.879848f,24.126032f,24.372217f,24.6184f,24.864584f,25.11077f,25.356953f,
            25.603136f,25.849321f,26.095505f,26.341688f,26.587873f,26.834057f,27.08024f,27.326426f,
            27.572609f,27.818792f,28.064978f,28.311161f,28.557344f,28.803528f,29.049713f,29.295897f,
            29.54208f,29.788265f,30.034449f,30.280632f,30.526817f,30.773f,31.019184f,31.26537f,
            31.511553f,31.757736f,32.00392f,32.250103f,32.49629f,32.742474f,32.988655f,33.23484f,
            33.481026f,33.727207f,33.973392f,34.219578f,34.46576f,34.711945f,34.95813f,35.20431f,
            35.450497f,35.696682f,35.942863f,36.18905f,36.435234f,36.681416f,36.9276f,37.173786f,
            37.419968f,37.666153f,37.91234f,38.15852f,38.404705f,38.65089f,38.89707f,39.143257f,
            39.389442f,39.635624f,39.88181f,40.127995f,40.374176f,40.62036f,40.866547f,41.11273f,
            41.358913f,41.6051f,41.85128f,42.097466f,42.34365f,42.589832f,42.836018f,43.082203f,
            43.328384f,43.57457f,43.820755f,44.066936f,44.31312f,44.559303f,44.80549f,45.051674f,
            45.297855f,45.54404f,45.790226f,46.036407f,46.282593f,46.528778f,46.77496f,47.021145f,
            47.26733f,47.51351f,47.759697f,48.005882f,48.252064f,48.49825f,48.744434f,48.990616f,
            49.2368f,49.482986f,49.729168f,49.975353f,50.22154f,50.46772f,50.713905f,50.96009f,
            51.206272f,51.452457f,51.698643f,51.944824f,52.19101f,52.437195f,52.683376f,52.92956f,
            53.175747f,53.42193f,53.668114f,53.9143f,54.16048f,54.406666f,54.65285f,54.899033f,
            55.145218f,55.391403f,55.637585f,55.88377f,56.129955f,56.376137f,56.622322f,56.868504f,
            57.11469f,57.360874f,57.607056f,57.85324f,58.099426f,58.345608f,58.591793f,58.83798f,
            59.08416f,59.330345f,59.57653f,59.822712f,60.068897f,60.315083f,60.561264f,60.80745f,
            61.053635f,61.299816f,61.546f,61.792187f,62.03837f,62.284554f,62.53074f,62.77692f
        },
        {
            0.0f,0.053275038f,0.106550075f,0.15982512f,0.21310015f,0.26637518f,0.31965023f,0.37292525f,
            0.4262003f,0.47947535f,0.53275037f,0.5860254f,0.63930047f,0.6925755f,0.7458505f,0.79912555f,
            0.8524006f,0.90567565f,0.9589507f,1.0122257f,1.0655007f,1.1187758f,1.1720508f,1.2253258f,
            1.2786009f,1.3318759f,1.385151f,1.438426f,1.491701f,1.5449761f,1.5982511f,1.6515262f,
            1.7048012f,1.7580762f,1.8113513f,1.8646263f,1.9179014f,1.9711764f,2.0244515f,2.0777264f,
            2.1310015f,2.1842766f,2.2375517f,2.2908266f,2.3441017f,2.3973768f,2.4506516f,2.5039268f,
            2.5572019f,2.6104767f,2.6637518f,2.717027f,2.770302f,2.823577f,2.876852f,2.9301271f,
            2.983402f,3.0366771f,3.0899522f,3.143227f,3.1965022f,3.2497773f,3.3030524f,3.3563273f,
            3.4096024f,3.4628775f,3.5161524f,3.5694275f,3.6227026f,3.6759777f,3.7292526f,3.7825277f,
            3.8358028f,3.8890777f,3.9423528f,3.9956279f,4.048903f,4.102178f,4.1554527f,4.208728f,
            4.262003f,4.315278f,4.368553f,4.4218283f,4.4751034f,4.528378f,4.581653f,4.634928f,
            4.6882033f,4.7414784f,4.7947536f,4.848028f,4.9013033f,4.9545784f,5.0078535f,5.0611286f,
            5.1144037f,5.167679f,5.2209535f,5.2742286f,5.3275037f,5.380779f,5.434054f,5.487329f,
            5.540604f,5.5938787f,5.647154f,5.700429f,5.753704f,5.806979f,5.8602543f,5.9135294f,
            5.966804f,6.020079f,6.0733542f,6.1266294f,6.1799045f,6.2331796f,6.286454f,6.3397293f,
            6.3930044f,6.4462795f,6.4995546f,6.5528297f,6.606105f,6.6593795f,6.7126546f,6.7659297f,
            6.819205f,6.87248f,6.925755f,6.97903f,7.032305f,7.08558f,7.138855f,7.19213f,
            7.245405f,7.2986803f,7.3519554f,7.40523f,7.458505f,7.5117803f,7.5650554f,7.6183305f,
            7.6716056f,7.72488f,7.7781553f,7.8314304f,7.8847055f,7.9379807f,7.9912558f,8.044531f,
            8.097806f,8.151081f,8.204356f,8.25763f,8.310905f,8.364181f,8.417456f,8.470731f,
            8.524006f,8.577281f,8.630556f,8.683831f,8.737106f,8.790381f,8.843657f,8.896932f,
            8.950207f,9.003481f,9.056756f,9.110031f,9.163306f,9.216581f,9.269856f,9.323132f,
            9.376407f,9.429682f,9.482957f,9.536232f,9.589507f,9.642782f,9.696056f,9.749331f,
            9.802607f,9.855882f,9.909157f,9.962432f,10.015707f,10.068982f,10.122257f,10.175532f,
            10.228807f,10.282083f,10.335358f,10.388633f,10.441907f,10.495182f,10.548457f,10.601732f,
            10.655007f,10.708282f,10.761558f,10.814833f,10.868108f,10.921383f,10.974658f,11.027933f,
            11.081208f,11.134482f,11.1877575f,11.241033f,11.294308f,11.347583f,11.400858f,11.454133f,
            11.507408f,11.560683f,11.613958f,11.667233f,11.720509f,11.773784f,11.827059f,11.880333f,
            11.933608f,11.986883f,12.040158f,12.093433f,12.1467085f,12.199984f,12.253259f,12.306534f,
            12.359809f,12.413084f,12.466359f,12.519634f,12.572908f,12.6261835f,12.679459f,12.732734f,
            12.786009f,12.839284f,12.892559f,12.945834f,12.999109f,13.052384f,13.1056595f,13.158935f,
            13.21221f,13.265485f,13.318759f,13.372034f,13.425309f,13.478584f,13.531859f,13.5851345f
        },
        {
            0.0f,0.0045689577f,0.009137915f,0.013706873f,0.01827583f,0.022844788f,0.027413746f,0.031982705f,
            0.03655166f,0.04112062f,0.045689575f,0.050258536f,0.054827493f,0.05939645f,0.06396541f,0.06853437f,
            0.07310332f,0.07767228f,0.08224124f,0.086810194f,0.09137915f,0.095948115f,0.10051707f,0.10508603f,
            0.109654985f,0.11422394f,0.1187929f,0.123361856f,0.12793082f,0.13249977f,0.13706873f,0.14163768f,
            0.14620665f,0.15077561f,0.15534456f,0.15991352f,0.16448247f,0.16905144f,0.17362039f,0.17818935f,
            0.1827583f,0.18732727f,0.19189623f,0.19646518f,0.20103414f,0.2056031f,0.21017206f,0.214741f,
            0.21930997f,0.22387893f,0.22844788f,0.23301685f,0.2375858f,0.24215476f,0.24672371f,0.25129268f,
            0.25586164f,0.2604306f,0.26499954f,0.2695685f,0.27413747f,0.27870643f,0.28327537f,0.28784433f,
            0.2924133f,0.29698226f,0.30155122f,0.30612016f,0.31068912f,0.3152581f,0.31982705f,0.32439598f,
            0.32896495f,0.3335339f,0.33810288f,0.34267184f,0.34724078f,0.35180974f,0.3563787f,0.36094767f,
            0.3655166f,0.37008557f,0.37465453f,0.3792235f,0.38379246f,0.3883614f,0.39293036f,0.39749932f,
            0.4020683f,0.40663725f,0.4112062f,0.41577515f,0.4203441f,0.42491308f,0.429482f,0.43405098f,
            0.43861994f,0.4431889f,0.44775787f,0.4523268f,0.45689577f,0.46146473f,0.4660337f,0.47060263f,
            0.4751716f,0.47974056f,0.48430952f,0.4888785f,0.49344742f,0.4980164f,0.50258535f,0.5071543f,
            0.5117233f,0.5162922f,0.5208612f,0.52543014f,0.5299991f,0.5345681f,0.539137f,0.54370594f,
            0.54827493f,0.55284387f,0.55741286f,0.5619818f,0.56655073f,0.5711197f,0.57568866f,0.58025765f,
            0.5848266f,0.5893955f,0.5939645f,0.59853345f,0.60310245f,0.6076714f,0.6122403f,0.6168093f,
            0.62137824f,0.62594724f,0.6305162f,0.6350851f,0.6396541f,0.64422303f,0.64879197f,0.65336096f,
            0.6579299f,0.6624989f,0.6670678f,0.67163676f,0.67620575f,0.6807747f,0.6853437f,0.6899126f,
            0.69448155f,0.69905055f,0.7036195f,0.7081885f,0.7127574f,0.71732634f,0.72189534f,0.7264643f,
            0.7310332f,0.7356022f,0.74017113f,0.7447401f,0.74930906f,0.753878f,0.758447f,0.7630159f,
            0.7675849f,0.77215385f,0.7767228f,0.7812918f,0.7858607f,0.7904297f,0.79499865f,0.7995676f,
            0.8041366f,0.8087055f,0.8132745f,0.81784344f,0.8224124f,0.82698137f,0.8315503f,0.83611923f,
            0.8406882f,0.84525716f,0.84982616f,0.8543951f,0.858964f,0.863533f,0.86810195f,0.87267095f,
            0.8772399f,0.8818088f,0.8863778f,0.89094675f,0.89551574f,0.9000847f,0.9046536f,0.9092226f,
            0.91379154f,0.91836053f,0.92292947f,0.9274984f,0.9320674f,0.9366363f,0.94120526f,0.94577426f,
            0.9503432f,0.9549122f,0.9594811f,0.96405005f,0.96861905f,0.973188f,0.977757f,0.9823259f,
            0.98689485f,0.99146384f,0.9960328f,1.0006018f,1.0051707f,1.0097396f,1.0143086f,1.0188776f,
            1.0234466f,1.0280155f,1.0325844f,1.0371534f,1.0417224f,1.0462914f,1.0508603f,1.0554292f,
            1.0599982f,1.0645672f,1.0691361f,1.0737051f,1.078274f,1.082843f,1.0874119f,1.0919809f,
            1.0965499f,1.1011188f,1.1056877f,1.1102567f,1.1148257f,1.1193947f,1.1239636f,1.1285325f,
            1.1331015f,1.1376705f,1.1422395f,1.1468084f,1.1513773f,1.1559463f,1.1605153f,1.1650842f
        }
    };

    private int shadowColor;

    public DropShadowEffect(Font font, int fontColor, int shadowColor) {
        super(font, fontColor);
        this.shadowColor = shadowColor;
    }

    public int getMaxHorizontalPadding() {
        return SHADOW_BLUR_MARGIN * 2;
    }

    public int getMaxVerticalPadding() {
        return SHADOW_BLUR_MARGIN * 2;
    }

    /**
     * Retrieves an ARGB integer array of the text with drop shadow effect.
     *
     * @param argbInfo the ARGB data to apply filter
     * @return the ARGB data that contains the given text
     */
    protected ARGBInfo getFilteredTextArgb(ARGBInfo argbInfo) {
        int[] extraPaddings = new int[4];
        ARGBInfo info = filterArgb(argbInfo, extraPaddings);

        stringPaddingLeft   = (stringPaddingLeft   <= 0) ? extraPaddings[0] : Math.min(stringPaddingLeft,   extraPaddings[0]);
        stringPaddingTop    = (stringPaddingTop    <= 0) ? extraPaddings[1] : Math.min(stringPaddingTop,    extraPaddings[1]);
        stringPaddingRight  = (stringPaddingRight  <= 0) ? extraPaddings[2] : Math.min(stringPaddingRight,  extraPaddings[2]);
        stringPaddingBottom = (stringPaddingBottom <= 0) ? extraPaddings[3] : Math.min(stringPaddingBottom, extraPaddings[3]);

        return info;
    }

    public Image filterImage(Image srcImg) {
        // Check whether the image has to be rerendered
        if (lastImage == srcImg) {
            return lastFilteredImage;
        }

        lastImage = srcImg;

        int[] srcArgb = new int[srcImg.getWidth() * srcImg.getHeight()];
        srcImg.getRGB(srcArgb, 0, srcImg.getWidth(), 0, 0, srcImg.getWidth(), srcImg.getHeight());

        int[] extraPaddings = new int[4];
        ARGBInfo srcInfo = new ARGBInfo(srcArgb, srcImg.getWidth(), srcImg.getHeight());
        ARGBInfo dstInfo = filterArgb(srcInfo, extraPaddings);

        imagePaddingLeft   = extraPaddings[0];
        imagePaddingTop    = extraPaddings[1];
        imagePaddingRight  = extraPaddings[2];
        imagePaddingBottom = extraPaddings[3];

        if (dstInfo == null) {
            lastFilteredImage = srcImg;
        } else {
            lastFilteredImage = Image.createRGBImage(dstInfo.argb, dstInfo.width, dstInfo.height, true);
        }

        return lastFilteredImage;
    }

    /**
     * Apply drop shadow effect on to ARGB data.
     *
     * @param srcInfo ARGB information of source pixels
     * @param paddings four elements integer array of the extra paddings
     * between source pixels and the resultant pixels.
     * @return the resultant pixels
     */
    private ARGBInfo filterArgb(ARGBInfo srcInfo, int[] paddings) {
        Rect srcContentRect = findContentBounds(srcInfo);

        if (srcContentRect.getWidth() <= 0 || srcContentRect.getHeight() <= 0) {
            return null;
        }

        int srcContentRight = srcContentRect.getLeft() + srcContentRect.getWidth();
        int srcContentBottom = srcContentRect.getTop() + srcContentRect.getHeight();

        // Calculate soft shadow size
        int shadowWidth = srcContentRect.getWidth() + getMaxHorizontalPadding();
        int shadowHeight = srcContentRect.getHeight() + getMaxVerticalPadding();

        // Calculate effect-applied image size, by ensuring contain whole soft shadow.
        int dstWidth = Math.max(srcInfo.width, shadowWidth);
        int dstHeight = Math.max(srcInfo.height, shadowHeight);

        int[] dstArgb = new int[dstWidth * dstHeight];

        // Calculate coordinates offset from source image to destination image.
        int srcDstXOffset = dstWidth <= srcInfo.width ? 0
                                : (dstWidth - srcInfo.width) / 2 - SHADOW_X_OFFSET;
        int srcDstYOffset = dstHeight <= srcInfo.height ? 0
                                : (dstHeight - srcInfo.height) / 2 - SHADOW_Y_OFFSET;

        paddings[0] = srcDstXOffset; // left extra padding
        paddings[1] = srcDstYOffset; // top extra padding
        paddings[2] = dstWidth - (srcInfo.width + srcDstXOffset); // right extra padding
        paddings[3] = dstHeight - (srcInfo.height + srcDstYOffset); // bottom extra padding

        // Calculate hard shadow bounds
        int shadowLeft = srcContentRect.getLeft() + srcDstXOffset + SHADOW_X_OFFSET;
        int shadowTop = srcContentRect.getTop() + srcDstYOffset + SHADOW_Y_OFFSET;

        int srcYOffset = srcContentRect.getTop() * srcInfo.width;
        int dstYOffset = shadowTop * dstWidth;
        int sx = 0;

        /**
         * A simple way of drawing image hard shadow:
         * <ul>
         * <li>The shadow has same size and shape as the opaque part of the
         * source image.</li>
         * <li>The shadow locates at specified position.</li>
         * <li>The shadow color is specified, while the opacity of destination
         * pixel is the product of the opacity of shadow color and the opacity
         * of source pixel.</li>
         * </ul>
         */
        for (int y = srcContentRect.getTop(); y < srcContentBottom; y++) {
            sx = shadowLeft;
            for (int x = srcContentRect.getLeft(); x < srcContentRight; x++) {
                /**
                 * This project only supports full opacity available pixels.
                 */
                if (DrawUtil.getAlpha(srcInfo.argb[srcYOffset + x]) == 0x0FF) {
                    dstArgb[dstYOffset + sx] = shadowColor;
                }
                sx++;
            }
            srcYOffset += srcInfo.width;
            dstYOffset += dstWidth;
        }

        /**
         * Draw soft shadow via Gaussian blur
         */
        int[] tempArgb = new int[dstWidth * dstHeight];
        convolveAlpha(KERNEL_MATRIX, dstArgb, tempArgb, dstWidth, dstHeight);
        convolveAlpha(KERNEL_MATRIX, tempArgb, dstArgb, dstHeight, dstWidth);

        /**
         * Copy source argb to destination argb
         */
        srcYOffset = srcContentRect.getTop() * srcInfo.width;
        dstYOffset = (srcContentRect.getTop() + srcDstYOffset) * dstWidth;
        int color = 0;
        for (int y = srcContentRect.getTop(); y < srcContentBottom; y++) {
            for (int x = srcContentRect.getLeft(); x < srcContentRight; x++) {
                color = srcInfo.argb[srcYOffset + x];
                if (DrawUtil.isOpacity(color)) {
                    dstArgb[dstYOffset + x + srcDstXOffset] = color;
                }
            }
            srcYOffset += srcInfo.width;
            dstYOffset += dstWidth;
        }

        return new ARGBInfo(dstArgb, dstWidth, dstHeight);
    }

    /**
     * Find the concrete content bounds in the specified image.
     *
     * @param argb the image to search
     * @param width the width of image.
     * @param height the height of image.
     * @return the bounds rectangle of the image concrete content.
     */
    private Rect findContentBounds(ARGBInfo info) {
        Rect rect = new Rect();

        boolean found = false;
        int yOffset = 0;

        // search top bound
        for (int y = 0; y < info.height; y++) {
            for (int x = 0; x < info.width; x++) {
                if (DrawUtil.isOpacity(info.argb[yOffset + x])) {
                    rect.moveTo(rect.getLeft(), y);
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
            yOffset += info.width;
        }

        // search bottom bound
        found = false;
        yOffset = (info.height - 1) * info.width;
        for (int y = info.height - 1; y >= 0; y--) {
            for (int x = 0; x < info.width; x++) {
                if (DrawUtil.isOpacity(info.argb[yOffset + x])) {
                    rect.changeSize(rect.getWidth(), y - rect.getTop() + 1);
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
            yOffset -= info.width;
        }

        int bottom = rect.getTop() + rect.getHeight();
        int left = info.width;
        int right = 0;
        yOffset = rect.getTop() * info.width;
        for (int y = rect.getTop(); y < bottom; y++) {
            // search left bound
            for (int x = 0; x < info.width; x++) {
                if (DrawUtil.isOpacity(info.argb[yOffset + x])) {
                    left = Math.min(left, x);
                    break;
                }
            }
            // search right bound
            for (int x = info.width - 1; x >= 0; x--) {
                if (DrawUtil.isOpacity(info.argb[yOffset + x])) {
                    right = Math.max(right, x);
                    break;
                }
            }
            yOffset += info.width;
        }
        rect.moveTo(left, rect.getTop());
        rect.changeSize(right - left + 1, rect.getHeight());

        return rect;
    }

    /**
     * Gaussian blur convolve shadow via alpha channel.
     *
     * @param matrix Gaussian blur kernel matrix
     * @param inPixels input ARGB pixels
     * @param outPixels output ARGB pixels
     * @param width the width of input ARGB pixels
     * @param height the height of input ARGB pixels
     */
    private void convolveAlpha(float[][] matrix, int[] inPixels, int[] outPixels, int width, int height) {
        int cols2 = matrix.length / 2;
        int index, ix, im, ia, pa;
        float a;

        for (int y = 0, yOffset = 0; y < height; y++, yOffset += width) {
            index = y;
            for (int x = 0; x < width; x++) {
                a = 0;
                im = 0;
                for (int col = -cols2; col <= cols2; col++, im++) {
                    ix = x + col;
                    // Ignore the pixel outside inPixels since it is considered as full trasparent.
                    if (ix >= 0 && ix < width) {
                        pa = DrawUtil.getAlpha(inPixels[yOffset + ix]);
                        if (pa > 0) {
                            a += matrix[im][pa];
                        }
                    }
                }
                ia = a > 254.5 ? 255 : (int)a;
                outPixels[index] = (ia << 24) | (inPixels[yOffset + x] & 0x0FFFFFF);
                index += height;
            }
        }
    }

    /**
     * Approximation of e^x. See
     * <a href="http://martin.ankerl.com/2007/10/04/optimized-pow-approximation-for-java-and-c-c/">
     * http://martin.ankerl.com/2007/10/04/optimized-pow-approximation-for-java-and-c-c/</a>
     *
     * @param a the exponent to raise <i>e</i> to.
     * @return the value <i>e</i><sup><code>a</code></sup>,
     * where <i>e</i> is the base of the natural logarithms.
     */
    private double exp(double a) {
//        final long tmp = (long) (1512775 * val + (1072693248 - 60801));
        final long tmp = (long) (1512775 * a + 1072632447);
        return Double.longBitsToDouble(tmp << 32);
    }

    /**
     * See {@link #SHADOW_BLUR_RADIUS}
     *
     * @param radius the blur radius
     */
    private void buildGaussianBlurKernelMatrix(int radius) {
        int rows = radius * 2 + 1;
        float[] kernel = new float[rows];
        float sigma = radius / 3;
        float sigma22 = 2 * sigma * sigma;
        float sqrtSigmaPi2 = (float) Math.sqrt(2 * Math.PI * sigma);
        float radius2 = radius * radius;
        float total = 0;
        int i = 0;
        for (int row = -radius; row <= radius; row++) {
            float distance = row * row;
            if (distance > radius2) {
                kernel[i] = 0;
            } else {
                kernel[i] = (float) exp(-(distance) / sigma22) / sqrtSigmaPi2;
            }
            total += kernel[i];
            i++;
        }
        for (i = 0; i < rows; i++) {
            kernel[i] /= total;
        }

        /**
         * Cache the product matrix of alpha value and Gaussian blur kernel.
         */
        float[][] matrix = new float[rows][256];
        for (i = 0; i < rows; i++) {
            for (int j = 0; j < 256; j++) {
                matrix[i][j] = kernel[i] * j;
            }
        }
    }
}
